package trab.lesw.curso;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CursoService {

    @Autowired
    private CursoRepository repository;

    public List<Curso> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Curso getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public String save(Curso curso) {
        boolean isNovo = curso.getId() == null;
        repository.save(curso);
        return isNovo ? "Curso criado com sucesso!" : "Curso atualizado com sucesso!";
    }

    public String delete(Long id) {
        try {
            repository.deleteById(id);
            return "Curso deletado com sucesso!";
        } catch (Exception e) {
            return "Não é possível excluir, o curso está em uso.";
        }
    }
}
