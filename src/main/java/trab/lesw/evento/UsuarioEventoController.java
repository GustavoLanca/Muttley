package trab.lesw.evento;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import trab.lesw.certificado.Certificado;
import trab.lesw.certificado.CertificadoRepository;
import trab.lesw.certificado.CertificadoService;
import trab.lesw.email.EmailService;
import trab.lesw.medalha.Medalha;
import trab.lesw.medalha.MedalhaRepository;
import trab.lesw.participacao.Participacao;
import trab.lesw.participacao.ParticipacaoRepository;
import trab.lesw.participacao.ParticipacaoService;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;
import trab.lesw.usuario.UsuarioService;

@Controller
@RequestMapping("/user/evento")
public class UsuarioEventoController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioEventoController.class);

    @Autowired
    private EventoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private ParticipacaoService participacaoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MedalhaRepository medalhaRepository;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private CertificadoService certificadoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping
    public String listar(Model model, HttpServletRequest request) {
        model.addAttribute("eventos", service.getAll());
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        model.addAttribute("baseUrl", baseUrl);
        return "user/eventos/listagem";
    }

    @GetMapping("/inscricao/{id}")
    public String inscricaoForm(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Evento evento = service.getById(id);
        if (!evento.isInscricaoAberta()) {
            attr.addFlashAttribute("erro", "As inscrições para este evento estão fechadas.");
            return "redirect:/user/evento";
        }
        model.addAttribute("evento", evento);
        return "evento/inscricao";
    }

    @PostMapping("/inscricao/{id}")
    public String inscrever(@PathVariable Long id, @RequestParam String cpf, RedirectAttributes attr) {
        Evento evento = service.getById(id);

        if (!evento.isInscricaoAberta()) {
            attr.addFlashAttribute("erro", "As inscrições para este evento estão fechadas.");
            return "redirect:/user/evento/inscricao/" + id;
        }

        Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "Digite um CPF válido.");
            return "redirect:/user/evento/inscricao/" + id;
        }

        Usuario usuario = opt.get();

        String msg = participacaoService.inscrever(usuario.getId(), id);
        if (msg.equals("Usuário já está inscrito nesse evento!")) {
            attr.addFlashAttribute("erro", msg);
            return "redirect:/user/evento/inscricao/" + id;
        }

        attr.addFlashAttribute("sucesso", msg);
        return "redirect:/user/evento";
    }

    @GetMapping("/confirmar/{id}")
    public String confirmarForm(@PathVariable Long id, Model model, RedirectAttributes attr) {
        Evento evento = service.getById(id);
        if (!evento.isConfirmacaoAberta()) {
            attr.addFlashAttribute("erro", "Confirmação fechada no momento. Verifique o período de confirmação do evento.");
            return "redirect:/user/evento";
        }
        model.addAttribute("evento", evento);
        return "evento/confirmar";
    }

    @PostMapping("/confirmar/{id}")
    public String confirmar(@PathVariable Long id, @RequestParam String cpf, RedirectAttributes attr) {
        Evento evento = service.getById(id);

        if (!evento.isConfirmacaoAberta()) {
            attr.addFlashAttribute("erro", "Confirmação fechada no momento. Verifique o período de confirmação do evento.");
            return "redirect:/user/evento/confirmar/" + id;
        }

        Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "Digite um CPF válido.");
            return "redirect:/user/evento/confirmar/" + id;
        }

        Usuario usuario = opt.get();

        String msg = participacaoService.participar(usuario.getId(), id);
        if (msg.equals("Usuário não está inscrito nesse evento!")) {
            attr.addFlashAttribute("erro", msg);
            return "redirect:/user/evento/confirmar/" + id;
        }
        if (msg.equals("Usuário já confirmou participação nesse evento!")) {
            attr.addFlashAttribute("erro", msg);
            return "redirect:/user/evento/confirmar/" + id;
        }

        if (!certificadoRepository.existsByUsuarioIdAndEventoId(usuario.getId(), id)) {
            try {
                byte[] pdfBytes = certificadoService.gerarCertificado(usuario, evento);
                if (pdfBytes.length > 0) {
                    String projectDir = System.getProperty("user.dir");
                    Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads",
                            "certificados");
                    Files.createDirectories(uploadPath);
                    String nomeArquivo = usuario.getId() + "_" + id + ".pdf";
                    Path destino = uploadPath.resolve(nomeArquivo);
                    Files.write(destino, pdfBytes);

                    emailService.enviarEmailComAnexo(usuario.getEmail(), "Certificado de Participação",
                            "Sua presença no evento " + evento.getTitulo() + " foi confirmada com sucesso. \n "
                                    + "Segue em anexo o seu certificado do evento.",
                            pdfBytes, "certificado.pdf");

                    Certificado cert = new Certificado();
                    cert.setUsuario(usuario);
                    cert.setEvento(evento);
                    cert.setArquivoPath("/uploads/certificados/" + nomeArquivo);
                    cert.setDataEmissao(LocalDateTime.now());
                    String dataEmissaoStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    cert.setHash(certificadoService.generateValidationHash(usuario, evento, dataEmissaoStr));
                    certificadoRepository.save(cert);
                }
            } catch (Exception e) {
                log.error("Erro ao gerar certificado automático", e);
            }
        }

        for (Usuario staff : evento.getOrganizadores()) {
            if (!certificadoRepository.existsByUsuarioIdAndEventoId(staff.getId(), id)) {
                gerarCertificado(staff, evento);
            }
        }
        for (Usuario staff : evento.getPalestrantes()) {
            if (!certificadoRepository.existsByUsuarioIdAndEventoId(staff.getId(), id)) {
                gerarCertificado(staff, evento);
            }
        }
        for (Usuario staff : evento.getProfessores()) {
            if (!certificadoRepository.existsByUsuarioIdAndEventoId(staff.getId(), id)) {
                gerarCertificado(staff, evento);
            }
        }

        return "redirect:/user/evento";
    }

    private void gerarCertificado(Usuario usuario, Evento evento) {
        log.info("Gerando certificado para usuario {} no evento {}", usuario.getId(), evento.getId());
        try {
            byte[] pdfBytes = certificadoService.gerarCertificado(usuario, evento);
            if (pdfBytes.length > 0) {
                String projectDir = System.getProperty("user.dir");
                Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads", "certificados");
                Files.createDirectories(uploadPath);
                String nomeArquivo = usuario.getId() + "_" + evento.getId() + ".pdf";
                Path destino = uploadPath.resolve(nomeArquivo);
                Files.write(destino, pdfBytes);

                emailService.enviarEmailComAnexo(usuario.getEmail(), "Certificado de Participação",
                        "Olá " + usuario.getNome() + ",\n\n"
                        + "Seu certificado de participação no evento \"" + evento.getTitulo() + "\" foi gerado com sucesso.\n"
                        + "Segue em anexo o seu certificado.\n\n"
                        + "Att,\nMuttley",
                        pdfBytes, "certificado.pdf");

                Certificado cert = new Certificado();
                cert.setUsuario(usuario);
                cert.setEvento(evento);
                cert.setArquivoPath("/uploads/certificados/" + nomeArquivo);
                cert.setDataEmissao(LocalDateTime.now());
                String dataEmissaoStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                cert.setHash(certificadoService.generateValidationHash(usuario, evento, dataEmissaoStr));
                certificadoRepository.save(cert);
            }
        } catch (Exception e) {
            log.error("Erro ao gerar certificado para usuario " + usuario.getId(), e);
        }
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("evento", service.getById(id));
        return "user/eventos/detalhes";
    }

    @GetMapping("/participacoes")
    public String paginaParticipacoes() {
        return "user/eventos/participacoes";
    }

    @PostMapping("/participacoes")
    public String buscarParticipacoes(@RequestParam String cpf, Model model) {
        model.addAttribute("cpf", cpf);
        Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
        
        if (opt.isEmpty()) {
            model.addAttribute("erro", "Digite um CPF válido.");
            return "user/eventos/participacoes";
        }

        Usuario usuario = opt.get();
        List<Participacao> participacoes = participacaoRepository.findByUsuarioId(usuario.getId());
        model.addAttribute("participacoes", participacoes);
        
        return "user/eventos/participacoes";
    }

    @GetMapping("/certificado")
    public String paginaMedalhas() {
        return "user/eventos/medalhas";
    }

    @PostMapping("/certificado")
    public String processarMedalhas(@RequestParam String cpf, RedirectAttributes attr) {
        Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
        
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "Digite um CPF válido.");
            return "redirect:/user/evento/certificado";
        }

        Usuario usuario = opt.get();
        String email = usuario.getEmail();

        List<Certificado> certificados = certificadoRepository.findByUsuarioIdWithEvento(usuario.getId());

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setSubject("Seus Certificados - Muttley");
            helper.setText("Olá " + usuario.getNome() + "!\n\nSegue em anexo os seus certificados.\n\nAtt,\nMuttley");

            for (Certificado c : certificados) {
                if (c.getEvento() != null) {
                    byte[] pdf = certificadoService.gerarCertificado(usuario, c.getEvento());
                    if (pdf.length > 0) {
                        String nomeArquivo = "certificado_" + c.getEvento().getTitulo().replaceAll("\\s+", "_") + ".pdf";
                        helper.addAttachment(nomeArquivo, new ByteArrayResource(pdf));
                    }
                }
            }
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("Erro ao enviar email com certificados", e);
            emailService.enviarEmail(email, "Seus Certificados - Muttley",
                    "Olá " + usuario.getNome() + "!\n\nSegue em anexo os seus certificados.\n\nAtt,\nMuttley");
        }

        attr.addFlashAttribute("sucesso", "Os seus certificados foram enviados para " + email);
        return "redirect:/user/evento/certificado";
    }

    @GetMapping("/certificado/validar")
    public String validarCertificadoForm(Model model) {
        return "user/eventos/validar-certificado";
    }

    @PostMapping("/certificado/validar")
    public String validarCertificado(@RequestParam String hash, RedirectAttributes attr) {
        Optional<Certificado> opt = certificadoRepository.findByHash(hash);
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "Hash inválido! Nenhum certificado encontrado com esse hash.");
            return "redirect:/user/evento/certificado/validar";
        }
        Certificado cert = opt.get();
        return "redirect:/evento/certificado/" + cert.getEvento().getId() + "/" + cert.getUsuario().getId();
    }

    @GetMapping("/cadastro")
    public String cadastroForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("publicForm", true);
        return "usuario/formulario";
    }

    @PostMapping("/cadastro")
    public String cadastroSalvar(@ModelAttribute Usuario usuario, RedirectAttributes attr) {
        String msg = usuarioService.save(usuario);
        if (msg.startsWith("CPF") || msg.startsWith("Digite")) {
            attr.addFlashAttribute("erro", msg);
            return "redirect:/user/evento/cadastro";
        }
        attr.addFlashAttribute("sucesso", msg);
        return "redirect:/user/evento";
    }
}