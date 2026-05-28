package trab.lesw.disciplina;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import trab.lesw.annotations.ApiKeyRequired;
import trab.lesw.annotations.RequiresPermission;

@RestController
@RequestMapping("/disciplinas")
@CrossOrigin("*")
public class DisciplinasController {
	@Autowired
	DisciplinaService dService;
	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	@GetMapping("/lista")
	public List<Disciplina> listagem(){
		return dService.getAll();
	}
	
	@PostMapping
	@RequiresPermission(any = {"MOD", "ADMIN"})
	@ApiKeyRequired
	@Transactional
	public ResponseEntity<?> cadastrar( @RequestBody @Valid Disciplina dados){
		dService.save(dados);
	    return ResponseEntity.status(HttpStatus.CREATED).body(dados);
	}
	
}
