package trab.lesw.usuario;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import trab.lesw.disciplina.DisciplinaRepository;
import trab.lesw.evento.Evento;
import trab.lesw.evento.EventoRepository;
import trab.lesw.medalha.MedalhaRepository;
import trab.lesw.participacao.ParticipacaoRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    @Autowired
    private MedalhaRepository medalhaRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    public List<Usuario> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Usuario getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public String save(Usuario usuario) {
    	boolean isNovo = usuario.getId() == null;
    	if (!isNovo && (usuario.getSenha() == null || usuario.getSenha().isBlank())) {
    	    Usuario existente = repository.findById(usuario.getId()).orElse(null);
    	    if (existente != null) {
    	        usuario.setSenha(existente.getSenha());
    	    }
    	}
    	repository.save(usuario);
    	return isNovo ? "Usuário criado com sucesso!" : "Usuário atualizado com sucesso!";
    }

    @Transactional
    public String delete(Long id) {
        participacaoRepository.findByUsuarioId(id).forEach(p -> participacaoRepository.delete(p));
        medalhaRepository.findByUsuarioId(id).forEach(m -> medalhaRepository.delete(m));

        List<Evento> eventos = eventoRepository.findAll();
        for (Evento e : eventos) {
            boolean changed = false;
            if (e.getOrganizadores().removeIf(u -> u.getId().equals(id))) changed = true;
            if (e.getPalestrantes().removeIf(u -> u.getId().equals(id))) changed = true;
            if (e.getProfessores().removeIf(u -> u.getId().equals(id))) changed = true;
            if (changed) eventoRepository.save(e);
        }

        disciplinaRepository.findByProfessorId(id).forEach(d -> {
            d.setProfessor(null);
            disciplinaRepository.save(d);
        });

        repository.deleteById(id);
        return "Usuário deletado!";
    }
}