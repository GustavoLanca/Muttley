package trab.lesw.tag;

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
@RequestMapping("/tags")
@CrossOrigin("*")
public class TagsController {
	@Autowired
	TagService tService;

	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	@GetMapping("/lista")
	public List<Tag> listagem() {
		return tService.getAll();
	}
	
	@PostMapping
	@RequiresPermission(any = {"MOD", "ADMIN"})
	@ApiKeyRequired
	@Transactional
	public ResponseEntity<?> cadastrar( @RequestBody @Valid Tag dados){
		tService.save(dados);
	    return ResponseEntity.status(HttpStatus.CREATED).body(dados);
	}
}
