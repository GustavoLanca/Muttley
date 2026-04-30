package trab.lesw.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private AlunoService alunoService;

    @GetMapping("/usuarios-medalhas")
    public String quadroMedalhas(Model model) {
        List<Usuario> usuarios = service.getAll();
        usuarios.forEach(u -> {
            if (u.getParticipacoes() != null) {
                u.getParticipacoes().size();
            }
            if (u.getMedalhas() != null) {
                u.getMedalhas().size();
            }
        });
        model.addAttribute("usuarios", usuarios);
        return "usuario/quadro-medalhas";
    }
}
