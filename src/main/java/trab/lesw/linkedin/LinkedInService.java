package trab.lesw.linkedin;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import trab.lesw.evento.Evento;
import trab.lesw.medalha.Medalha;
import trab.lesw.medalha.MedalhaService;
import trab.lesw.tag.Tag;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Service
public class LinkedInService {

    private static final Logger log = LoggerFactory.getLogger(LinkedInService.class);

    @Autowired
    private LinkedInConfig config;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedalhaService medalhaService;

    private static final String AUTH_URL = "https://www.linkedin.com/oauth/v2/authorization";
    private static final String TOKEN_URL = "https://www.linkedin.com/oauth/v2/accessToken";
    private static final String API_BASE = "https://api.linkedin.com";
    private static final String SCOPES = "openid profile email w_member_social";

    public String getAuthorizationUrl(Long usuarioId) {
        return AUTH_URL + "?response_type=code"
            + "&client_id=" + config.getClientId()
            + "&redirect_uri=" + URLEncoder.encode(config.getRedirectUri(), StandardCharsets.UTF_8)
            + "&scope=" + SCOPES.replace(" ", "%20")
            + "&state=" + usuarioId;
    }

    public TokenResponse exchangeCodeForToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", config.getRedirectUri());
        body.add("client_id", config.getClientId());
        body.add("client_secret", config.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            TokenResponse tr = new TokenResponse();
            tr.accessToken = root.get("access_token").asText();
            if (root.has("expires_in")) {
                tr.expiresIn = root.get("expires_in").asLong();
            }
            return tr;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar token do LinkedIn", e);
        }
    }

    public String getPersonId(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            API_BASE + "/v2/userinfo", HttpMethod.GET, request, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.get("sub").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter ID do usuário no LinkedIn", e);
        }
    }

    public void addSkill(String accessToken, String personId, String skillName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        headers.set("LinkedIn-Version", "202506");

        String body = "{\"name\":{\"localized\":{\"en_US\":\"" + escapeJson(skillName) + "\"}}}";
        HttpEntity<String> request = new HttpEntity<>(body, headers);

        String url = API_BASE + "/v2/people/(id:" + personId + ")/skills";
        restTemplate.postForEntity(url, request, String.class);
    }

    public String exportMedals(Usuario usuario) {
        try {
            if (usuario.getLinkedinToken() == null) {
                return "Usuário não autenticado com LinkedIn.";
            }

            String accessToken = usuario.getLinkedinToken();
            String personId = usuario.getLinkedinPersonId();

            if (personId == null) {
                personId = getPersonId(accessToken);
                usuario.setLinkedinPersonId(personId);
                usuarioRepository.save(usuario);
            }

            List<Medalha> medalhas = medalhaService.getMedalhasByUsuarioId(usuario.getId());
            if (medalhas.isEmpty()) {
                return "Nenhuma medalha encontrada para exportar.";
            }

            int count = 0;
            int errors = 0;
            for (Medalha m : medalhas) {
                try {
                    addSkill(accessToken, personId, m.getNome());
                    count++;
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg != null && msg.contains("409")) {
                        count++;
                    } else {
                        errors++;
                        log.error("Erro ao exportar medalha '{}' para LinkedIn: {}", m.getNome(), msg);
                    }
                }
            }
            String resultado = count + " medalha(s) exportada(s) como competência(s) no LinkedIn!";
            if (errors > 0) {
                resultado += " (" + errors + " falha(s) ignorada(s))";
            }
            return resultado;
        } catch (Exception e) {
            return "Erro ao exportar medalhas: " + e.getMessage();
        }
    }

    public String publishEvent(Usuario usuario, Evento evento) {
        try {
            if (usuario.getLinkedinToken() == null) {
                return "Usuário não autenticado com LinkedIn.";
            }

            String accessToken = usuario.getLinkedinToken();
            String personId = usuario.getLinkedinPersonId();

            if (personId == null) {
                personId = getPersonId(accessToken);
                usuario.setLinkedinPersonId(personId);
                usuarioRepository.save(usuario);
            }

            String texto = evento.getMensagemPublicacao();
            if (texto == null || texto.isBlank()) {
                texto = "Participei do evento: " + evento.getTitulo() + "!";
            }

            if (evento.getTags() != null && !evento.getTags().isEmpty()) {
                StringBuilder tags = new StringBuilder();
                for (Tag tag : evento.getTags()) {
                    tags.append(" #").append(tag.getNome().replace(" ", ""));
                }
                texto += "\n\n" + tags.toString();
            }

            String authorUrn = "urn:li:person:" + personId;
            String mediaUrn = null;

            if (evento.getImagemUrl() != null && !evento.getImagemUrl().isBlank()) {
                mediaUrn = uploadImage(accessToken, authorUrn, evento.getImagemUrl());
            }

            String postBody = buildPostJsonString(authorUrn, texto, mediaUrn);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("LinkedIn-Version", "202506");
            headers.set("X-Restli-Protocol-Version", "2.0.0");

            HttpEntity<String> request = new HttpEntity<>(postBody, headers);
            restTemplate.postForEntity(API_BASE + "/v2/ugcPosts", request, String.class);

            return "Evento publicado no LinkedIn com sucesso!";
        } catch (Exception e) {
            log.error("Erro ao publicar evento no LinkedIn", e);
            return "Erro ao publicar no LinkedIn: " + e.getMessage();
        }
    }

    private String uploadImage(String accessToken, String authorUrn, String imagemUrl) throws IOException {
        String projectDir = System.getProperty("user.dir");
        Path imgPath = Paths.get(projectDir, "src", "main", "resources", "static", imagemUrl.startsWith("/") ? imagemUrl.substring(1) : imagemUrl);
        log.info("Buscando imagem em: {}", imgPath.toAbsolutePath());
        if (!Files.exists(imgPath)) {
            log.warn("Imagem não encontrada: {}", imgPath.toAbsolutePath());
            return null;
        }

        String registerBody = "{"
            + "\"registerUploadRequest\":{"
            + "\"recipes\":[\"urn:li:digitalmediaRecipe:feedshare-image\"],"
            + "\"owner\":\"" + escapeJson(authorUrn) + "\","
            + "\"serviceRelationships\":[{"
            + "\"relationshipType\":\"OWNER\","
            + "\"identifier\":\"urn:li:userGeneratedContent\"}]}}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<String> registerRequest = new HttpEntity<>(registerBody, headers);
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
            API_BASE + "/v2/assets?action=registerUpload", registerRequest, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode registerResult = mapper.readTree(registerResponse.getBody());
        String uploadUrl = registerResult.path("value").path("uploadMechanism")
            .path("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest").path("uploadUrl").asText();
        String asset = registerResult.path("value").path("asset").asText();

        if (uploadUrl == null || uploadUrl.isBlank()) {
            log.error("Falha ao obter uploadUrl do LinkedIn");
            return null;
        }

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setContentType(MediaType.IMAGE_JPEG);
        uploadHeaders.setBearerAuth(accessToken);

        byte[] imageBytes = Files.readAllBytes(imgPath);
        HttpEntity<byte[]> uploadEntity = new HttpEntity<>(imageBytes, uploadHeaders);
        restTemplate.exchange(uploadUrl, HttpMethod.PUT, uploadEntity, String.class);

        return asset;
    }

    private String buildPostJsonString(String authorUrn, String texto, String mediaUrn) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"author\":\"").append(escapeJson(authorUrn)).append("\",");
        json.append("\"lifecycleState\":\"PUBLISHED\",");
        json.append("\"specificContent\":{");
        json.append("\"com.linkedin.ugc.ShareContent\":{");
        json.append("\"shareCommentary\":{\"text\":\"").append(escapeJson(texto)).append("\"},");
        if (mediaUrn != null) {
            json.append("\"shareMediaCategory\":\"IMAGE\",");
            json.append("\"media\":[{\"status\":\"READY\",");
            json.append("\"description\":{\"text\":\"\"},");
            json.append("\"media\":\"").append(escapeJson(mediaUrn)).append("\"}]");
        } else {
            json.append("\"shareMediaCategory\":\"NONE\"");
        }
        json.append("}}");
        json.append(",\"visibility\":{\"com.linkedin.ugc.MemberNetworkVisibility\":\"CONNECTIONS\"}");
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static class TokenResponse {
        public String accessToken;
        public long expiresIn;
    }
}
