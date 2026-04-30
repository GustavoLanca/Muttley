package trab.lesw.evento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import trab.lesw.tag.Tag;
import trab.lesw.tag.TagRepository;
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;
    
    @Autowired
    private TagRepository tagRepository;
    public List<Evento> getAll() {
        return eventoRepository.findAll(Sort.by("data").descending());
    }

    public Evento getById(Long id) {
        return eventoRepository.getReferenceById(id);
    }

    public String save(Evento evento, List<Long> tagIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            evento.setTags(tags);
        } else {
            evento.setTags(new ArrayList<>());
        }
        eventoRepository.save(evento);
        return "Evento salvo!";
    }

    public String delete(Long id) {
    	eventoRepository.deleteById(id);
        return "Evento deletado!";
    }
}