package trab.lesw.evento;

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
    public String inscricaoForm(@PathVariable Long id, RedirectAttributes attr) {
        Evento evento = service.getById(id);
        if (!evento.isInscricaoAberta()) {
            attr.addFlashAttribute("erro", "As inscrições para este evento estão fechadas.");
            return "redirect:/user/evento";
        }
        return "redirect:/evento/inscricao/" + id;
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

    @GetMapping("/medalhas")
    public String paginaMedalhas() {
        return "user/eventos/medalhas";
    }

    @PostMapping("/medalhas")
    public String processarMedalhas(@RequestParam String cpf, RedirectAttributes attr) {
        Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
        
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "Digite um CPF válido.");
            return "redirect:/user/evento/medalhas";
        }

        Usuario usuario = opt.get();
        String email = usuario.getEmail();

        List<Medalha> medalhas = medalhaRepository.findByUsuarioId(usuario.getId());
        List<Certificado> certificados = certificadoRepository.findByUsuarioIdWithEvento(usuario.getId());

        StringBuilder corpo = new StringBuilder();
        corpo.append("Olá ").append(usuario.getNome()).append("!\n\n");

        corpo.append("=== MEDALHAS ===\n");
        if (medalhas.isEmpty()) {
            corpo.append("Nenhuma medalha conquistada ainda.\n");
        } else {
            for (Medalha m : medalhas) {
                corpo.append("- ").append(m.getNome());
                if (m.getEvento() != null) {
                    corpo.append(" (").append(m.getEvento().getTitulo()).append(")");
                }
                corpo.append("\n");
            }
        }

        corpo.append("\n=== CERTIFICADOS ===\n");
        if (certificados.isEmpty()) {
            corpo.append("Nenhum certificado emitido ainda.\n");
        } else {
            corpo.append("Total de certificados: ").append(certificados.size()).append("\n\n");
        }

        corpo.append("Att,\nMuttley");

        if (certificados.isEmpty()) {
            emailService.enviarEmail(email, "Suas Medalhas e Certificados - Muttley", corpo.toString());
        } else {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(email);
                helper.setSubject("Suas Medalhas e Certificados - Muttley");
                helper.setText(corpo.toString());

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
                emailService.enviarEmail(email, "Suas Medalhas e Certificados - Muttley", corpo.toString());
            }
        }

        attr.addFlashAttribute("sucesso", "O relatório com as suas medalhas e certificados foi enviado para " + email);
        return "redirect:/user/evento/medalhas";
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