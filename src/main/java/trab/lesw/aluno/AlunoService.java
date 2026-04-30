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

    @Transactional
    public List<Aluno> getAll() {
        List<Aluno> alunos = repository.findAllWithUsuario();
        alunos.forEach(a -> {
            if (a.getUsuario() != null && a.getUsuario().getMedalhas() != null) {
                a.getUsuario().getMedalhas().size();
            }
        });
        return alunos;
    }

    @Transactional
    public Aluno getById(Long id) {
        Aluno aluno = repository.getReferenceById(id);
        if (aluno.getUsuario() != null) {
            if (aluno.getUsuario().getParticipacoes() != null) {
                aluno.getUsuario().getParticipacoes().size();
            }
            if (aluno.getUsuario().getMedalhas() != null) {
                aluno.getUsuario().getMedalhas().size();
            }
        }
        return aluno;
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
