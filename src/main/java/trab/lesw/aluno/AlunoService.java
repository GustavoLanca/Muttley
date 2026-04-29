package trab.lesw.aluno;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class AlunoService {

    @Autowired
    private AlunoRepository repository;

    public List<Aluno> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Aluno getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Transactional
    public String save(Aluno aluno) {
        repository.save(aluno);
        return aluno.getId() != null ?
                "Aluno atualizado com sucesso!" :
                "Aluno criado com sucesso!";
    }

    @Transactional
    public String delete(Long id) {
        repository.deleteById(id);
        return "Aluno deletado!";
    }
}
