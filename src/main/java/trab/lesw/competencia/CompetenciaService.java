package trab.lesw.competencia;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trab.lesw.evento.EventoRepository;
import trab.lesw.tag.TagRepository;

@Service
public class CompetenciaService {

    @Autowired
    private CompetenciaRepository repository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EventoRepository eventoRepository;

    public String associar(Long tagId, Long eventoId) {
        Competencia c = new Competencia();
        c.setTag(tagRepository.getReferenceById(tagId));
        c.setEvento(eventoRepository.getReferenceById(eventoId));
        repository.save(c);
        return "Competência associada!";
    }

    public List<Competencia> getAll() {
        return repository.findAllDetalhado();
    }

    public List<Competencia> getByTag(Long tagId) {
        return repository.findByTagId(tagId);
    }

    public List<Competencia> getByEvento(Long eventoId) {
        return repository.findByEventoId(eventoId);
    }
}
