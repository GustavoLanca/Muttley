package trab.lesw.evento;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import trab.lesw.participacao.Participacao;
import trab.lesw.participacao.ParticipacaoRepository;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/user/evento")
public class UsuarioEventoController {

    @Autowired
    private EventoService service;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

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
            model.addAttribute("erro", "CPF não encontrado no sistema.");
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
    public String processarMedalhas(@RequestParam String email, RedirectAttributes attr) {
        Optional<Usuario> opt = usuarioRepository.findByEmail(email);
        
        if (opt.isEmpty()) {
            attr.addFlashAttribute("erro", "E-mail não encontrado no sistema.");
            return "redirect:/user/evento/medalhas";
        }

        attr.addFlashAttribute("sucesso", "O relatório com as suas medalhas foi gerado e enviado para o email informado");
        return "redirect:/user/evento/medalhas";
    }
}