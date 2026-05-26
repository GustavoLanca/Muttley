package trab.lesw.linkedin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/linkedin")
public class LinkedInController {

    @Autowired
    private LinkedInService linkedInService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/auth")
    public RedirectView auth(@RequestParam Long usuarioId) {
        String url = linkedInService.getAuthorizationUrl(usuarioId);
        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public String callback(@RequestParam String code,
                           @RequestParam String state,
                           RedirectAttributes attr) {
        try {
            Long usuarioId = Long.parseLong(state);

            LinkedInService.TokenResponse tokenResponse = linkedInService.exchangeCodeForToken(code);
            String personId = linkedInService.getPersonId(tokenResponse.accessToken);

            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario == null) {
                attr.addFlashAttribute("message", "Usuário não encontrado.");
                return "redirect:/usuario";
            }

            usuario.setLinkedinToken(tokenResponse.accessToken);
            usuario.setLinkedinTokenExpires(
                System.currentTimeMillis() / 1000 + tokenResponse.expiresIn);
            usuario.setLinkedinPersonId(personId);
            usuarioRepository.save(usuario);

            String result = linkedInService.exportMedals(usuario);
            attr.addFlashAttribute("message", result);
        } catch (Exception e) {
            attr.addFlashAttribute("message", "Erro na integração com LinkedIn: " + e.getMessage());
        }
        return "redirect:/usuario";
    }
}
