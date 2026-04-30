package trab.lesw.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lista", service.getAll());
        return "usuario/listagem";
    }

    @GetMapping("/medalhas")
    public String quadroMedalhas(Model model) {
        model.addAttribute("usuarios", service.getAll());
        return "usuario/quadro-medalhas";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editarFormulario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", service.getById(id));
        return "usuario/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario) {
        String message = service.save(usuario);
        return "redirect:/usuario?message=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/usuario?message=" + java.net.URLEncoder.encode("Usuário deletado!", java.nio.charset.StandardCharsets.UTF_8);
    }
}
