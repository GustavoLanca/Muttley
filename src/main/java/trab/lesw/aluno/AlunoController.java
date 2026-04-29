package trab.lesw.aluno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "aluno/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno,
                         RedirectAttributes attr) {
        String msg = service.save(aluno);
        attr.addFlashAttribute("message", msg);
        return "redirect:/aluno";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/aluno";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("aluno", service.getById(id));
        return "aluno/formulario";
    }
}
