package trab.lesw.participacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import trab.lesw.evento.EventoRepository;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/participacao")
public class ParticipacaoController {

    @Autowired
    private ParticipacaoService service;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EventoRepository eventoRepository;
    
    
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "participacao/listagem";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        model.addAttribute("eventos", eventoRepository.findAll());
        return "participacao/formulario";
    }

    @PostMapping("/salvar")
    public String participar(@RequestParam Long usuarioId,
                             @RequestParam Long eventoId,
                             RedirectAttributes attr) {

        attr.addFlashAttribute("message",
                service.participar(usuarioId, eventoId));

        return "redirect:/participacao";
    }

    @GetMapping("/pontos/{usuarioId}")
    public String verPontos(@PathVariable Long usuarioId, Model model) {
        model.addAttribute("usuario", usuarioRepository.getReferenceById(usuarioId));
        model.addAttribute("totalPontos", service.calcularTotalPontos(usuarioId));
        model.addAttribute("participacoes", service.getAll());
        return "participacao/pontos";
    }
}