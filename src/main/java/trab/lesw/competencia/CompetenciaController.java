package trab.lesw.competencia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/competencia")
public class CompetenciaController {

    @Autowired
    private CompetenciaService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "competencia/listagem";
    }

    @GetMapping("/formulario")
    public String formulario() {
        return "competencia/formulario";
    }

    @PostMapping("/salvar")
    public String associar(@RequestParam Long tagId,
                           @RequestParam Long eventoId,
                           RedirectAttributes attr) {
        attr.addFlashAttribute("message",
                service.associar(tagId, eventoId));
        return "redirect:/competencia";
    }
}
