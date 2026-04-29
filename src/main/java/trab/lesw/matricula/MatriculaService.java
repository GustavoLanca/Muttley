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
        Matricula m = new Matricula();
        m.setAluno(alunoRepository.getReferenceById(alunoId));
        m.setDisciplina(disciplinaRepository.getReferenceById(disciplinaId));
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
