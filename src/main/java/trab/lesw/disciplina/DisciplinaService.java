package trab.lesw.disciplina;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    public Disciplina getById(Long id) {
        return repository.getReferenceById(id);
    }

    public List<Disciplina> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    @Transactional
    public String save(Disciplina disciplina) {
        repository.save(disciplina);
        return disciplina.getId() != null ?
                "Disciplina atualizada com sucesso!" :
                "Disciplina criada com sucesso!";
    }

    @Transactional
    public String delete(Long id) {
        repository.deleteById(id);
        return "Disciplina deletada!";
    }
}