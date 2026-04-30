package trab.lesw.matricula;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trab.lesw.aluno.AlunoRepository;
import trab.lesw.disciplina.DisciplinaRepository;

@Service
public class MatriculaService {

    @Autowired
    private MatriculaRepository repository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public String matricular(Long alunoId, Long disciplinaId) {
        var alunoOpt = alunoRepository.findById(alunoId);
        var disciplinaOpt = disciplinaRepository.findById(disciplinaId);
        if (alunoOpt.isEmpty()) {
            return "Erro: Aluno não encontrado!";
        }
        if (disciplinaOpt.isEmpty()) {
            return "Erro: Disciplina não encontrada!";
        }
        Matricula m = new Matricula();
        m.setAluno(alunoOpt.get());
        m.setDisciplina(disciplinaOpt.get());
        repository.save(m);
        return "Matrícula realizada!";
    }

    public List<Matricula> getAll() {
        return repository.findAllAlunoDisciplina();
    }

    public List<Matricula> getMatriculasByAluno(Long alunoId) {
        return repository.findByAlunoId(alunoId);
    }

    public List<Matricula> getMatriculasByDisciplina(Long disciplinaId) {
        return repository.findByDisciplinaId(disciplinaId);
    }
}
