package trab.lesw.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Transactional
    public List<Usuario> getAll() {
        List<Usuario> usuarios = repository.findAllWithMedalhas();
        usuarios.forEach(u -> {
            if (u.getParticipacoes() != null) {
                u.getParticipacoes().size();
            }
        });
        return usuarios;
    }

    @Transactional
    public Usuario getById(Long id) {
        Usuario usuario = repository.getReferenceById(id);
        if (usuario.getParticipacoes() != null) {
            usuario.getParticipacoes().size();
        }
        if (usuario.getMedalhas() != null) {
            usuario.getMedalhas().size();
        }
        return usuario;
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