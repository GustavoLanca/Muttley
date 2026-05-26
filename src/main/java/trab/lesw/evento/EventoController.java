package trab.lesw.evento;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.linkedin.LinkedInService;
import trab.lesw.participacao.ParticipacaoService;
import trab.lesw.tag.TagRepository;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/evento")
public class EventoController {

	private static final Logger log = LoggerFactory.getLogger(EventoController.class);

	@Autowired
	private EventoService service;
	
	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ParticipacaoService participacaoService;

	@Autowired
	private LinkedInService linkedInService;

	@Autowired
	private CertificadoService certificadoService;

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("lista", service.getAll());
		String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		model.addAttribute("baseUrl", baseUrl);
		return "evento/listagem";
	}

	@GetMapping("/formulario")
	public String novo(Model model) {
	    model.addAttribute("evento", new Evento());
	    model.addAttribute("tags", tagRepository.findAll());
	    model.addAttribute("disciplinas", disciplinaRepository.findAll());
	    model.addAttribute("organizadores", usuarioRepository.findByTipo("ORGANIZADOR"));
	    model.addAttribute("palestrantes", usuarioRepository.findByTipo("PALESTRANTE"));
	    model.addAttribute("professores", usuarioRepository.findByTipo("PROFESSOR"));
	    return "evento/formulario";
	}
	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Evento evento,
						 @RequestParam(required = false) List<Long> tags,
						 @RequestParam(required = false) List<Long> organizadores,
						 @RequestParam(required = false) List<Long> palestrantes,
						 @RequestParam(required = false) List<Long> professores,
						 @RequestParam(required = false) MultipartFile imagemFile,
						 RedirectAttributes attr) {
		if (imagemFile != null && !imagemFile.isEmpty()) {
			try {
				String projectDir = System.getProperty("user.dir");
				Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads", "eventos");
				Files.createDirectories(uploadPath);
				String nomeArquivo = System.currentTimeMillis() + "_" + imagemFile.getOriginalFilename();
				Path destino = uploadPath.resolve(nomeArquivo);
				Files.copy(imagemFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
				evento.setImagemUrl("/uploads/eventos/" + nomeArquivo);
			} catch (Exception e) {
				attr.addFlashAttribute("message", "Erro ao salvar imagem: " + e.getMessage());
			}
		}
		attr.addFlashAttribute("message", service.save(evento, tags, organizadores, palestrantes, professores));
		return "redirect:/evento";
	}

	@GetMapping("/delete/{id}")
	@Transactional
	public String delete(@PathVariable Long id, RedirectAttributes attr) {
		attr.addFlashAttribute("message", service.delete(id));
		return "redirect:/evento";
	}

	@GetMapping("/formulario/{id}")
	public String editar(@PathVariable Long id, Model model) {
	    model.addAttribute("evento", service.getById(id));
	    model.addAttribute("tags", tagRepository.findAll());
	    model.addAttribute("disciplinas", disciplinaRepository.findAll());
	    model.addAttribute("organizadores", usuarioRepository.findByTipo("ORGANIZADOR"));
	    model.addAttribute("palestrantes", usuarioRepository.findByTipo("PALESTRANTE"));
	    model.addAttribute("professores", usuarioRepository.findByTipo("PROFESSOR"));
	    return "evento/formulario";
	}

	@GetMapping("/confirmar/{id}")
	public String confirmarForm(@PathVariable Long id, Model model) {
		model.addAttribute("evento", service.getById(id));
		return "evento/confirmar";
	}

	@PostMapping("/confirmar/{id}")
	public String confirmar(@PathVariable Long id,
							@RequestParam String email,
							@RequestParam String senha,
							RedirectAttributes attr) {
		Evento evento = service.getById(id);

		Optional<Usuario> opt = usuarioRepository.findByEmailAndSenha(email, senha);
		if (opt.isEmpty()) {
			attr.addFlashAttribute("erro", "E-mail ou senha inválidos.");
			return "redirect:/evento/confirmar/" + id;
		}

		Usuario usuario = opt.get();
		if (!usuario.getTipo().equals("ALUNO") && !usuario.getTipo().equals("EXTERNO")) {
			attr.addFlashAttribute("erro", "Apenas alunos e externos podem confirmar participação.");
			return "redirect:/evento/confirmar/" + id;
		}

		String msg = participacaoService.participar(usuario.getId(), id);
		if (msg.equals("Usuário já está inscrito nesse evento!")) {
			attr.addFlashAttribute("erro", msg);
			return "redirect:/evento/confirmar/" + id;
		}

		String linkedinMsg = linkedInService.publishEvent(usuario, evento);
		attr.addFlashAttribute("linkedinMsg", linkedinMsg);

		return "redirect:/evento/confirmado/" + id + "/" + usuario.getId();
	}

	@GetMapping("/confirmado/{id}/{usuarioId}")
	public String confirmado(@PathVariable Long id, @PathVariable Long usuarioId, Model model) {
		model.addAttribute("evento", service.getById(id));
		model.addAttribute("usuario", usuarioRepository.findById(usuarioId).orElse(null));
		model.addAttribute("downloadUrl", "/evento/certificado/" + id + "/" + usuarioId);
		return "evento/confirmado";
	}

	@GetMapping("/certificado/{eventoId}/{usuarioId}")
	public ResponseEntity<InputStreamResource> certificado(@PathVariable Long eventoId, @PathVariable Long usuarioId) {
		try {
			Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioId);
			if (optUsuario.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			Evento evento = service.getById(eventoId);
			if (evento == null) {
				return ResponseEntity.notFound().build();
			}

			byte[] pdfBytes = certificadoService.gerarCertificado(optUsuario.get(), evento);
			if (pdfBytes.length == 0) {
				return ResponseEntity.internalServerError().build();
			}

			ByteArrayInputStream in = new ByteArrayInputStream(pdfBytes);
			InputStreamResource resource = new InputStreamResource(in);
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(resource);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}