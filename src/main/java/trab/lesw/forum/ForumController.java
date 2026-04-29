package trab.lesw.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/forum")
public class ForumController {

    @Autowired
    private ForumService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "forum/listagem";
    }

    @GetMapping("/formulario")
    public String formulario() {
        return "forum/formulario";
    }

    @PostMapping("/salvar")
    public String registrar(@RequestParam Long alunoId,
                            @RequestParam Long eventoId,
                            @RequestParam Long disciplinaId,
                            @RequestParam Integer semestre,
                            RedirectAttributes attr) {
        attr.addFlashAttribute("message",
                service.registrar(alunoId, eventoId, disciplinaId, semestre));
        return "redirect:/forum";
    }
}
