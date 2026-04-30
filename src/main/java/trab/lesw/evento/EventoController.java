package trab.lesw.evento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.transaction.Transactional;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.tag.TagRepository;

@Controller
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private EventoService service;
	
	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("lista", service.getAll());
		return "evento/listagem";
	}

	@GetMapping("/formulario")
	public String novo(Model model) {
	    model.addAttribute("evento", new Evento());
	    model.addAttribute("tags", tagRepository.findAll());
	    model.addAttribute("disciplinas", disciplinaRepository.findAll());
	    return "evento/formulario";
	}
	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Evento evento, @RequestParam(required = false) List <Long>tags, RedirectAttributes attr) {
		attr.addFlashAttribute("message", service.save(evento, tags));
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
	    return "evento/formulario";
	}
}