package trab.lesw.matricula;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/matricula")
public class MatriculaController {

    @Autowired
    private MatriculaService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "matricula/listagem";
    }

    @GetMapping("/formulario")
    public String formulario() {
        return "matricula/formulario";
    }

    @PostMapping("/salvar")
    public String matricular(@RequestParam Long alunoId,
                             @RequestParam Long disciplinaId,
                             RedirectAttributes attr) {
        attr.addFlashAttribute("message",
                service.matricular(alunoId, disciplinaId));
        return "redirect:/matricula";
    }
}
