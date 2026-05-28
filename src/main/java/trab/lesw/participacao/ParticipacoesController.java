package trab.lesw.participacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import trab.lesw.annotations.ApiKeyRequired;
import trab.lesw.annotations.RequiresPermission;

@RestController
@RequestMapping("/participacoes")
@CrossOrigin("*")
public class ParticipacoesController {
	@Autowired
	private ParticipacaoService ptService;
	
	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	@PostMapping("/lista")
	public List<Participacao> listagem(@RequestBody Participacao dados) {
		return ptService.participacoes(dados.getId());
	}
}
