package trab.lesw.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;
import trab.lesw.annotations.ApiKeyRequired;
import trab.lesw.annotations.RequiresPermission;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuariosController {
	@Autowired
	UsuarioService uService;
	@RequiresPermission(any = { "MOD", "ADMIN" })
	@ApiKeyRequired
	@Transactional
	@GetMapping("/lista")
	public List<Usuario> listagem(){
		return uService.getAll();
	}
}
