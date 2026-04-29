package trab.lesw.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "tag/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("tag", new Tag());
        return "tag/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Tag tag,
                         RedirectAttributes attr) {
        String msg = service.save(tag);
        attr.addFlashAttribute("message", msg);
        return "redirect:/tag";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/tag";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("tag", service.getById(id));
        return "tag/formulario";
    }
}
