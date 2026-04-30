package trab.lesw.forum;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trab.lesw.aluno.AlunoRepository;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.evento.EventoRepository;

@Service
public class ForumService {

    @Autowired
    private ForumRepository repository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public String registrar(Long alunoId, Long eventoId, Long disciplinaId, Integer semestre) {
        var alunoOpt = alunoRepository.findById(alunoId);
        var eventoOpt = eventoRepository.findById(eventoId);
        var disciplinaOpt = disciplinaRepository.findById(disciplinaId);
        if (alunoOpt.isEmpty()) {
            return "Erro: Aluno não encontrado!";
        }
        if (eventoOpt.isEmpty()) {
            return "Erro: Evento não encontrado!";
        }
        if (disciplinaOpt.isEmpty()) {
            return "Erro: Disciplina não encontrada!";
        }
        Forum f = new Forum();
        f.setAluno(alunoOpt.get());
        f.setEvento(eventoOpt.get());
        f.setDisciplina(disciplinaOpt.get());
        f.setSemestre(semestre);
        repository.save(f);
        return "Fórum registrado!";
    }

    public List<Forum> getAll() {
        return repository.findAllDetalhado();
    }

    public List<Forum> getByAluno(Long alunoId) {
        return repository.findByAlunoId(alunoId);
    }

    public List<Forum> getByEvento(Long eventoId) {
        return repository.findByEventoId(eventoId);
    }

    public List<Forum> getByDisciplina(Long disciplinaId) {
        return repository.findByDisciplinaId(disciplinaId);
    }
}
