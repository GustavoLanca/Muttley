package trab.lesw.evento;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import trab.lesw.disciplina.Disciplina;
import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.medalha.MedalhaRepository;
import trab.lesw.participacao.ParticipacaoRepository;
import trab.lesw.tag.Tag;
import trab.lesw.tag.TagRepository;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;
    
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private MedalhaRepository medalhaRepository;

    public List<Evento> getAll() {
        return eventoRepository.findAll(Sort.by("data").descending());
    }

    public Evento getById(Long id) {
        return eventoRepository.getReferenceById(id);
    }

    public String save(Evento evento, List<Long> tagIds, List<Long> organizadorIds, List<Long> palestranteIds, List<Long> professorIds) {
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            evento.setTags(tags);
        } else {
            evento.setTags(new ArrayList<>());
        }
        if (evento.getDisciplina() != null && evento.getDisciplina().getId() != null) {
            Disciplina disciplina = disciplinaRepository.getReferenceById(evento.getDisciplina().getId());
            evento.setDisciplina(disciplina);
        } else {
            evento.setDisciplina(null);
        }
        if (organizadorIds != null && !organizadorIds.isEmpty()) {
            evento.setOrganizadores(usuarioRepository.findAllById(organizadorIds));
        } else {
            evento.setOrganizadores(new ArrayList<>());
        }
        if (palestranteIds != null && !palestranteIds.isEmpty()) {
            evento.setPalestrantes(usuarioRepository.findAllById(palestranteIds));
        } else {
            evento.setPalestrantes(new ArrayList<>());
        }
        if (professorIds != null && !professorIds.isEmpty()) {
            evento.setProfessores(usuarioRepository.findAllById(professorIds));
        } else {
            evento.setProfessores(new ArrayList<>());
        }
        evento.setPontos(1);
        eventoRepository.save(evento);
        return "Evento salvo!";
    }

    public String delete(Long id) {
        medalhaRepository.findByEventoId(id).forEach(m -> medalhaRepository.delete(m));
        participacaoRepository.findByEventoIdWithUsuario(id).forEach(p -> participacaoRepository.delete(p));
    	eventoRepository.deleteById(id);
        return "Evento deletado!";
    }
}