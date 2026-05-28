package trab.lesw.disciplina;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;
 
@Service
public class DisciplinaService {
 
    @Autowired
    private DisciplinaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;
 
    public List<Disciplina> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }
 
    public Disciplina getById(Long id) {
        return repository.getReferenceById(id);
    }
 
    public String save(Disciplina disciplina) {
        boolean isNovo = disciplina.getId() == null;

        if (disciplina.getProfessor() != null && disciplina.getProfessor().getId() != null) {
            Usuario professor = usuarioRepository.findById(disciplina.getProfessor().getId()).orElse(null);
            disciplina.setProfessor(professor);
        } else {
            disciplina.setProfessor(null);
        }

        repository.save(disciplina);
        return isNovo ? "Disciplina criada" : "Disciplina atualizada";
    }
 
    public String delete(Long id) {
        try {
            repository.deleteById(id);
            return "Disciplina deletada";
        } catch (Exception e) {
            return "Não é possível excluir, está em uso";
        }
    }
}