package trab.lesw.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Usuario getById(Long id) {
        return repository.getReferenceById(id);
    }

    public String save(Usuario usuario) {
        repository.save(usuario);
        return usuario.getId() != null ?
                "Usuário atualizado com sucesso!" :
                "Usuário criado com sucesso!";
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Usuário deletado!";
    }
}