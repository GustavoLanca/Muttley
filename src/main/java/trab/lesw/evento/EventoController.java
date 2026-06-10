package trab.lesw.evento;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import trab.lesw.certificado.Certificado;
import trab.lesw.certificado.CertificadoRepository;
import trab.lesw.certificado.CertificadoService;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.email.EmailService;
import trab.lesw.linkedin.LinkedInService;
import trab.lesw.participacao.Participacao;
import trab.lesw.participacao.ParticipacaoRepository;
import trab.lesw.participacao.ParticipacaoService;
import trab.lesw.tag.TagRepository;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Controller
@RequestMapping("/evento")
public class EventoController {

	private static final Logger log = LoggerFactory.getLogger(EventoController.class);

	@Autowired
	private EventoService service;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private EmailService emailService;

	@Autowired
	private DisciplinaRepository disciplinaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ParticipacaoService participacaoService;

	@Autowired
	private ParticipacaoRepository participacaoRepository;

	@Autowired
	private LinkedInService linkedInService;

	@Autowired
	private CertificadoService certificadoService;

	@Autowired
	private CertificadoRepository certificadoRepository;

	@GetMapping
	public String listar(Model model, HttpServletRequest request) {
		model.addAttribute("lista", service.getAll());
		String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
		model.addAttribute("baseUrl", baseUrl);
		return "evento/listagem";
	}

	@GetMapping("/participantes/{id}")
	public String participantes(@PathVariable Long id, Model model) {
		Evento evento = service.getById(id);
		model.addAttribute("evento", evento);

		List<Participacao> inscritos = participacaoRepository.findByEventoIdAndConfirmado(id, false);
		List<Participacao> confirmados = participacaoRepository.findByEventoIdAndConfirmado(id, true);

		model.addAttribute("inscritos", inscritos);
		model.addAttribute("totalInscritos", inscritos.size());
		model.addAttribute("confirmados", confirmados);
		model.addAttribute("totalConfirmados", confirmados.size());

		return "evento/participantes";
	}

	@GetMapping("/formulario")
	public String novo(Model model) {
		model.addAttribute("evento", new Evento());
		model.addAttribute("tags", tagRepository.findAll());
		model.addAttribute("disciplinas", disciplinaRepository.findAll());
		model.addAttribute("organizadores", usuarioRepository.findAll());
		model.addAttribute("palestrantes", usuarioRepository.findAll());
		model.addAttribute("professores", usuarioRepository.findAll());
		return "evento/formulario";
	}

	@PostMapping("/salvar")
	public String salvar(@ModelAttribute Evento evento, @RequestParam(required = false) List<Long> tags,
			@RequestParam(required = false) List<Long> organizadores,
			@RequestParam(required = false) List<Long> palestrantes,
			@RequestParam(required = false) List<Long> professores,
			@RequestParam(required = false) MultipartFile imagemFile,
			@RequestParam(required = false) String inicioInscricaoDate,
			@RequestParam(required = false) String inicioInscricaoTime,
			@RequestParam(required = false) String fimInscricaoDate,
			@RequestParam(required = false) String fimInscricaoTime,
			@RequestParam(required = false) String inicioConfirmacaoDate,
			@RequestParam(required = false) String inicioConfirmacaoTime,
			@RequestParam(required = false) String fimConfirmacaoDate,
			@RequestParam(required = false) String fimConfirmacaoTime, RedirectAttributes attr) {
		if (inicioInscricaoDate != null && inicioInscricaoTime != null && !inicioInscricaoDate.isEmpty()
				&& !inicioInscricaoTime.isEmpty()) {
			evento.setInicioInscricao(
					LocalDateTime.of(LocalDate.parse(inicioInscricaoDate), LocalTime.parse(inicioInscricaoTime)));
		} else {
			evento.setInicioInscricao(null);
		}
		if (fimInscricaoDate != null && fimInscricaoTime != null && !fimInscricaoDate.isEmpty()
				&& !fimInscricaoTime.isEmpty()) {
			evento.setFimInscricao(
					LocalDateTime.of(LocalDate.parse(fimInscricaoDate), LocalTime.parse(fimInscricaoTime)));
		} else {
			evento.setFimInscricao(null);
		}
		if (inicioConfirmacaoDate != null && inicioConfirmacaoTime != null && !inicioConfirmacaoDate.isEmpty()
				&& !inicioConfirmacaoTime.isEmpty()) {
			evento.setInicioConfirmacao(
					LocalDateTime.of(LocalDate.parse(inicioConfirmacaoDate), LocalTime.parse(inicioConfirmacaoTime)));
		} else {
			evento.setInicioConfirmacao(null);
		}
		if (fimConfirmacaoDate != null && fimConfirmacaoTime != null && !fimConfirmacaoDate.isEmpty()
				&& !fimConfirmacaoTime.isEmpty()) {
			evento.setFimConfirmacao(
					LocalDateTime.of(LocalDate.parse(fimConfirmacaoDate), LocalTime.parse(fimConfirmacaoTime)));
		} else {
			evento.setFimConfirmacao(null);
		}
		if (imagemFile != null && !imagemFile.isEmpty()) {
			try {
				String projectDir = System.getProperty("user.dir");
				Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads", "eventos");
				Files.createDirectories(uploadPath);
				String nomeArquivo = System.currentTimeMillis() + "_" + imagemFile.getOriginalFilename();
				Path destino = uploadPath.resolve(nomeArquivo);
				Files.copy(imagemFile.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
				evento.setImagemUrl("/uploads/eventos/" + nomeArquivo);
			} catch (Exception e) {
				attr.addFlashAttribute("message", "Erro ao salvar imagem: " + e.getMessage());
			}
		}
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
		model.addAttribute("organizadores", usuarioRepository.findAll());
		model.addAttribute("palestrantes", usuarioRepository.findAll());
		model.addAttribute("professores", usuarioRepository.findAll());
		return "evento/formulario";
	}

	@GetMapping("/inscricao/{id}")
	public String inscricaoForm(@PathVariable Long id, Model model, RedirectAttributes attr) {
		Evento evento = service.getById(id);
		if (!evento.isInscricaoAberta()) {
			attr.addFlashAttribute("erro",
					"Inscrições fechadas no momento. Verifique o período de inscrição do evento.");
			return "redirect:/evento";
		}
		model.addAttribute("evento", evento);
		return "evento/inscricao";
	}

	@PostMapping("/inscricao/{id}")
	public String inscrever(@PathVariable Long id, @RequestParam String cpf, RedirectAttributes attr) {
		Evento evento = service.getById(id);

		if (!evento.isInscricaoAberta()) {
			attr.addFlashAttribute("erro",
					"Inscrições fechadas no momento. Verifique o período de inscrição do evento.");
			return "redirect:/evento/inscricao/" + id;
		}

		Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
		if (opt.isEmpty()) {
			attr.addFlashAttribute("erro", "CPF não encontrado.");
			return "redirect:/evento/inscricao/" + id;
		}

		Usuario usuario = opt.get();

		String msg = participacaoService.inscrever(usuario.getId(), id);
		if (msg.equals("Usuário já está inscrito nesse evento!")) {
			attr.addFlashAttribute("erro", msg);
			return "redirect:/evento/inscricao/" + id;
		}

		attr.addFlashAttribute("sucesso", msg);
		return "redirect:/evento/inscricao/" + id;
	}

	@GetMapping("/confirmar/{id}")
	public String confirmarForm(@PathVariable Long id, Model model, RedirectAttributes attr) {
		Evento evento = service.getById(id);
		if (!evento.isConfirmacaoAberta()) {
			attr.addFlashAttribute("erro",
					"Confirmação fechada no momento. Verifique o período de confirmação do evento.");
			return "redirect:/evento";
		}
		model.addAttribute("evento", evento);
		return "evento/confirmar";
	}

	@PostMapping("/confirmar/{id}")
	public String confirmar(@PathVariable Long id, @RequestParam String cpf, RedirectAttributes attr) {
		Evento evento = service.getById(id);

		if (!evento.isConfirmacaoAberta()) {
			attr.addFlashAttribute("erro",
					"Confirmação fechada no momento. Verifique o período de confirmação do evento.");
			return "redirect:/evento/confirmar/" + id;
		}

		Optional<Usuario> opt = usuarioRepository.findByCpf(cpf);
		if (opt.isEmpty()) {
			attr.addFlashAttribute("erro", "CPF não encontrado.");
			return "redirect:/evento/confirmar/" + id;
		}

		Usuario usuario = opt.get();

		String msg = participacaoService.participar(usuario.getId(), id);
		if (msg.equals("Usuário não está inscrito nesse evento!")) {
			attr.addFlashAttribute("erro", msg);
			return "redirect:/evento/confirmar/" + id;
		}
		if (msg.equals("Usuário já confirmou participação nesse evento!")) {
			attr.addFlashAttribute("erro", msg);
			return "redirect:/evento/confirmar/" + id;
		}

		if (Boolean.FALSE.equals(certificadoRepository.existeCertificadoProcedure(usuario.getId(), id))) {
			try {
				byte[] pdfBytes = certificadoService.gerarCertificado(usuario, evento);
				if (pdfBytes.length > 0) {
					String projectDir = System.getProperty("user.dir");
					Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads",
							"certificados");
					Files.createDirectories(uploadPath);
					String nomeArquivo = usuario.getId() + "_" + id + ".pdf";
					Path destino = uploadPath.resolve(nomeArquivo);
					Files.write(destino, pdfBytes);

					Certificado cert = new Certificado();
					cert.setUsuario(usuario);
					cert.setEvento(evento);
					cert.setArquivoPath("/uploads/certificados/" + nomeArquivo);
					cert.setDataEmissao(LocalDateTime.now());
					certificadoRepository.save(cert);
					emailService.enviarEmailComAnexo(usuario.getEmail(), "Certificado de Participação",
							"Sua presença no evento " + evento.getTitulo() + " foi confirmada com sucesso. \n "
									+ "Segue em anexo o seu certificado do evento.",
							pdfBytes, "certificado.pdf"); 
				}
			} catch (Exception e) {
				log.error("Erro ao gerar certificado automático", e);
			}
		}

		return "redirect:/linkedin/auth?usuarioId=" + usuario.getId() + "&eventoId=" + id;
	}

	@GetMapping("/confirmado/{id}/{usuarioId}")
	public String confirmado(@PathVariable Long id, @PathVariable Long usuarioId, Model model) {
		model.addAttribute("evento", service.getById(id));
		model.addAttribute("usuario", usuarioRepository.findById(usuarioId).orElse(null));
		model.addAttribute("downloadUrl", "/evento/certificado/" + id + "/" + usuarioId);
		return "evento/confirmado";
	}

	@GetMapping("/certificado/{eventoId}/{usuarioId}")
	public ResponseEntity<InputStreamResource> certificado(@PathVariable Long eventoId, @PathVariable Long usuarioId) {
		try {
			Optional<Usuario> optUsuario = usuarioRepository.findById(usuarioId);
			if (optUsuario.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			Evento evento = service.getById(eventoId);
			if (evento == null) {
				return ResponseEntity.notFound().build();
			}

			byte[] pdfBytes = certificadoService.gerarCertificado(optUsuario.get(), evento);
			if (pdfBytes.length == 0) {
				return ResponseEntity.internalServerError().build();
			}

			if (Boolean.FALSE.equals(certificadoRepository.existeCertificadoProcedure(usuarioId, eventoId))) {
				String projectDir = System.getProperty("user.dir");
				Path uploadPath = Paths.get(projectDir, "src", "main", "resources", "static", "uploads",
						"certificados");
				Files.createDirectories(uploadPath);
				String nomeArquivo = usuarioId + "_" + eventoId + ".pdf";
				Path destino = uploadPath.resolve(nomeArquivo);
				Files.write(destino, pdfBytes);

				Certificado cert = new Certificado();
				cert.setUsuario(optUsuario.get());
				cert.setEvento(evento);
				cert.setArquivoPath("/uploads/certificados/" + nomeArquivo);
				cert.setDataEmissao(LocalDateTime.now());
				certificadoRepository.save(cert);
			}

			ByteArrayInputStream in = new ByteArrayInputStream(pdfBytes);
			InputStreamResource resource = new InputStreamResource(in);
			return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificado.pdf")
					.contentType(MediaType.APPLICATION_PDF).body(resource);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}