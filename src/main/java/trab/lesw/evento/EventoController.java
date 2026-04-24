package trab.lesw.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "evento/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("evento", new Evento());
        return "evento/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Evento evento,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.save(evento));
        return "redirect:/evento";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/evento";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("evento", service.getById(id));
        return "evento/formulario";
    }
}