package trab.lesw.evento;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoService {

    @Autowired
    private EventoRepository repository;

    public List<Evento> getAll() {
        return repository.findAll();
    }

    public Evento getById(Long id) {
        return repository.getReferenceById(id);
    }

    public String save(Evento evento) {
        repository.save(evento);
        return "Evento salvo!";
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Evento deletado!";
    }
}