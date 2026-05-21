package trab.lesw.evento;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.participacao.ParticipacaoService;
import trab.lesw.tag.TagRepository;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private EventoService service;
	
	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ParticipacaoService participacaoService;

	@Autowired
	private ResourceLoader resourceLoader;

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("lista", service.getAll());
		String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		model.addAttribute("baseUrl", baseUrl);
		return "evento/listagem";
	}

	@GetMapping("/formulario")
	public String novo(Model model) {
	    model.addAttribute("evento", new Evento());
	    model.addAttribute("tags", tagRepository.findAll());
	    model.addAttribute("disciplinas", disciplinaRepository.findAll());
	    model.addAttribute("organizadores", usuarioRepository.findByTipo("ORGANIZADOR"));
	    model.addAttribute("palestrantes", usuarioRepository.findByTipo("PALESTRANTE"));
	    model.addAttribute("professores", usuarioRepository.findByTipo("PROFESSOR"));
	    return "evento/formulario";
	}
	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Evento evento,
						 @RequestParam(required = false) List<Long> tags,
						 @RequestParam(required = false) List<Long> organizadores,
						 @RequestParam(required = false) List<Long> palestrantes,
						 @RequestParam(required = false) List<Long> professores,
						 RedirectAttributes attr) {
		attr.addFlashAttribute("message", service.save(evento, tags, organizadores, palestrantes, professores));
		return "redirect:/evento";
	}

	@GetMapping("/delete/{id}")
	@Transactional
	public String delete(@PathVariable Long id, RedirectAttributes attr) {
		attr.addFlashAttribute("message", service.delete(id));
		return "redirect:/evento";
	}

	@GetMapping("/formulario/{id}")
	public String editar(@PathVariable Long id, Model model) {
	    model.addAttribute("evento", service.getById(id));
	    model.addAttribute("tags", tagRepository.findAll());
	    model.addAttribute("disciplinas", disciplinaRepository.findAll());
	    model.addAttribute("organizadores", usuarioRepository.findByTipo("ORGANIZADOR"));
	    model.addAttribute("palestrantes", usuarioRepository.findByTipo("PALESTRANTE"));
	    model.addAttribute("professores", usuarioRepository.findByTipo("PROFESSOR"));
	    return "evento/formulario";
	}

	@GetMapping("/confirmar/{id}")
	public String confirmarForm(@PathVariable Long id, Model model) {
		model.addAttribute("evento", service.getById(id));
		return "evento/confirmar";
	}

	@PostMapping("/confirmar/{id}")
	public String confirmar(@PathVariable Long id,
							@RequestParam String email,
							@RequestParam String senha,
							Model model) {
		Evento evento = service.getById(id);
		model.addAttribute("evento", evento);

		Optional<Usuario> opt = usuarioRepository.findByEmailAndSenha(email, senha);
		if (opt.isEmpty()) {
			model.addAttribute("erro", "E-mail ou senha inválidos.");
			return "evento/confirmar";
		}

		Usuario usuario = opt.get();
		if (!usuario.getTipo().equals("ALUNO") && !usuario.getTipo().equals("EXTERNO")) {
			model.addAttribute("erro", "Apenas alunos e externos podem confirmar participação.");
			return "evento/confirmar";
		}

		String msg = participacaoService.participar(usuario.getId(), id);
		if (msg.equals("Usuário já está inscrito nesse evento!")) {
			model.addAttribute("erro", msg);
			return "evento/confirmar";
		}

		return "redirect:/evento/confirmado/" + id;
	}

	@GetMapping("/confirmado/{id}")
	public String confirmado(@PathVariable Long id, Model model) {
		model.addAttribute("evento", service.getById(id));
		model.addAttribute("downloadUrl", "/evento/certificado/" + id);
		return "evento/confirmado";
	}

	@GetMapping("/certificado/{eventoId}")
	public ResponseEntity<InputStreamResource> certificado(@PathVariable Long eventoId) {
		try {
			InputStream in = getClass().getClassLoader().getResourceAsStream("templates/certificado.pdf");
			if (in == null) {
				return ResponseEntity.notFound().build();
			}
			InputStreamResource resource = new InputStreamResource(in);
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(resource);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}