package trab.lesw.usuario;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import trab.lesw.certificado.Certificado;
import trab.lesw.certificado.CertificadoRepository;
import trab.lesw.medalha.MedalhaService;
import trab.lesw.participacao.ParticipacaoService;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private ParticipacaoService participacaoService;

    @Autowired
    private MedalhaService medalhaService;

    @Autowired
    private CertificadoRepository certificadoRepository;

    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = service.getAll();
        Map<Long, Integer> pontosMap = usuarios.stream()
            .collect(Collectors.toMap(
                Usuario::getId,
                u -> participacaoService.calcularTotalPontos(u.getId())
            ));
        Map<Long, List<trab.lesw.medalha.Medalha>> medalhasMap = usuarios.stream()
            .collect(Collectors.toMap(
                Usuario::getId,
                u -> medalhaService.getMedalhasByUsuarioId(u.getId())
            ));
        Map<Long, List<Certificado>> certificadosMap = usuarios.stream()
            .collect(Collectors.toMap(
                Usuario::getId,
                u -> certificadoRepository.findByUsuarioIdWithEvento(u.getId())
            ));
        Map<Long, String> certificadosDataMap = new HashMap<>();
        for (Usuario u : usuarios) {
            List<Certificado> certs = certificadosMap.get(u.getId());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < certs.size(); i++) {
                if (i > 0) sb.append("||");
                sb.append(certs.get(i).getEvento().getId())
                  .append("|")
                  .append(certs.get(i).getEvento().getTitulo());
            }
            certificadosDataMap.put(u.getId(), sb.toString());
        }
        model.addAttribute("lista", usuarios);
        model.addAttribute("pontosMap", pontosMap);
        model.addAttribute("medalhasMap", medalhasMap);
        model.addAttribute("certificadosDataMap", certificadosDataMap);
        return "usuario/listagem";
    }

    @GetMapping("/formulario")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Usuario usuario,
                         RedirectAttributes attr) {
        String msg = service.save(usuario);
        if (msg.startsWith("CPF") || msg.startsWith("Digite")) {
            attr.addFlashAttribute("erro", msg);
            return "redirect:/usuario/formulario" + (usuario.getId() != null ? "/" + usuario.getId() : "");
        }
        attr.addFlashAttribute("message", msg);
        return "redirect:/usuario";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable Long id,
                         RedirectAttributes attr) {
        attr.addFlashAttribute("message", service.delete(id));
        return "redirect:/usuario";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = service.getById(id);
        model.addAttribute("usuario", usuario);
        return "usuario/formulario";
    }
}
