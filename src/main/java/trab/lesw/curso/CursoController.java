package trab.lesw.curso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/curso")
public class CursoController {

    @Autowired
    private CursoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "curso/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("curso", new Curso());
        return "curso/formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("curso", service.getById(id));
        return "curso/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Curso curso, RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.save(curso));
        return "redirect:/curso";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id, RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/curso";
    }
}
