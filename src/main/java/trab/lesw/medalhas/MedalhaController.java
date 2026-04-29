package trab.lesw.medalhas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import trab.lesw.usuario.UsuarioService;

@Controller
@RequestMapping("/medalha")
public class MedalhaController {

    @Autowired
    private MedalhaService service;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "medalha/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("medalha", new Medalha());
        return "medalha/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Medalha medalha,
                         RedirectAttributes attr) {
        String msg = service.save(medalha);
        attr.addFlashAttribute("message", msg);
        return "redirect:/medalha";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/medalha";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medalha", service.getById(id));
        return "medalha/formulario";
    }
}