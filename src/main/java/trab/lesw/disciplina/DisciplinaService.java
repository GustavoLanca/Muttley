package trab.lesw.disciplina;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
 
@Service
public class DisciplinaService {
 
    @Autowired
    private DisciplinaRepository repository;
 
    public List<Disciplina> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }
 
    public Disciplina getById(Long id) {
        return repository.getReferenceById(id);
    }
 
    public String save(Disciplina disciplina) {
        boolean isNovo = disciplina.getId() == null;
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