package trab.lesw.evento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trab.lesw.competencia.Competencia;
import trab.lesw.competencia.CompetenciaRepository;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repository;

    @Autowired
    private CompetenciaRepository competenciaRepository;

public List<Evento> getAll() {
    return repository.findAllWithCompetencias();
}

public TipoEvento[] getTiposEvento() {
    return TipoEvento.values();
}

    public Evento getById(Long id) {
        return repository.getReferenceById(id);
    }

@Transactional
public String save(Evento evento) {
    if (evento.getCompetencias() != null && !evento.getCompetencias().isEmpty()) {
        List<Competencia> competencias = competenciaRepository.findAllById(
            evento.getCompetencias().stream().map(Competencia::getId).toList()
        );
        evento.setCompetencias(competencias);
    } else {
        evento.setCompetencias(null);
    }
    repository.save(evento);
    return "Evento salvo!";
}

public List<Evento> getEventosComTipo() {
    List<Evento> eventos = repository.findAllWithCompetencias();
    eventos.forEach(e -> {
        if (e.getTipoEvento() == null) {
            e.setTipoEvento(TipoEvento.OUTRO);
        }
    });
    return eventos;
}

    public String delete(Long id) {
        repository.deleteById(id);
        return "Evento deletado!";
    }
}