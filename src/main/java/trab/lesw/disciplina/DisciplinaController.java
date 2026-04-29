package trab.lesw.disciplina;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/disciplina")
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "disciplina/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("disciplina", new Disciplina());
        return "disciplina/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Disciplina disciplina,
                         RedirectAttributes attr) {
        String msg = service.save(disciplina);
        attr.addFlashAttribute("message", msg);
        return "redirect:/disciplina";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/disciplina";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("disciplina", service.getById(id));
        return "disciplina/formulario";
    }
}