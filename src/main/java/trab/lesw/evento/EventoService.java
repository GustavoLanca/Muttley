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
        return eventoRepository.findById(id).orElseThrow(() -> new RuntimeException("Evento não encontrado: " + id));
    }

    public String save(Evento formEvento, List<Long> tagIds, List<Long> organizadorIds, List<Long> palestranteIds, List<Long> professorIds) {
        Evento evento;
        if (formEvento.getId() != null) {
            evento = eventoRepository.findById(formEvento.getId()).orElse(null);
            if (evento == null) {
                return "Evento não encontrado!";
            }
            evento.setTitulo(formEvento.getTitulo());
            evento.setDescricao(formEvento.getDescricao());
            evento.setTipo(formEvento.getTipo());
            evento.setData(formEvento.getData());
            evento.setHoraInicio(formEvento.getHoraInicio());
            evento.setHoraFim(formEvento.getHoraFim());
            evento.setImagemUrl(formEvento.getImagemUrl());
            evento.setMensagemPublicacao(formEvento.getMensagemPublicacao());
            evento.setPublicado(formEvento.getPublicado());
            evento.setInicioInscricao(formEvento.getInicioInscricao());
            evento.setFimInscricao(formEvento.getFimInscricao());
            evento.setInicioConfirmacao(formEvento.getInicioConfirmacao());
            evento.setFimConfirmacao(formEvento.getFimConfirmacao());
        } else {
            evento = formEvento;
            evento.setPontos(1);
        }
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            evento.setTags(tags);
        } else {
            evento.setTags(new ArrayList<>());
        }
        if (evento.getDisciplina() != null && evento.getDisciplina().getId() != null) {
            Disciplina disciplina = disciplinaRepository.findById(evento.getDisciplina().getId()).orElse(null);
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