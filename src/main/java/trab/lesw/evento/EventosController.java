package trab.lesw.evento;

/*
 * caso queira inserir/alterar só mandar o POST para localhost:8080/eventos
 * caso queira listar mande o GET para localhost:8080/eventos/lista
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trab.lesw.annotations.ApiKeyRequired;
import trab.lesw.annotations.RequiresPermission;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/eventos")
@CrossOrigin("*")
public class EventosController {
	@Autowired
	private EventoService evService;

	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	@GetMapping("/lista")
	public List<Evento> listagem() {
		return evService.getAll();
	}

	@PostMapping
	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	public ResponseEntity<?> cadastrar(@RequestBody @Valid Evento dados,
			@RequestParam(required = false) List<Long> tagIds,

			@RequestParam(required = false) List<Long> organizadorIds,

			@RequestParam(required = false) List<Long> palestranteIds,

			@RequestParam(required = false) List<Long> professorIds) {

		evService.save(dados, tagIds, organizadorIds, palestranteIds, professorIds);
		return ResponseEntity.status(HttpStatus.CREATED).body(dados);
	}
}
