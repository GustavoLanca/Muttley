package trab.lesw.participacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/participacao")
public class ParticipacaoController {

    @Autowired
    private ParticipacaoService service;
    
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "participacao/listagem";
    }

    @GetMapping("/formulario")
    public String formulario() {
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
}