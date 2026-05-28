package trab.lesw.participacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trab.lesw.medalha.MedalhaService;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;
import trab.lesw.evento.Evento;
import trab.lesw.evento.EventoRepository;

@Service
public class ParticipacaoService {

	@Autowired
	private ParticipacaoRepository repository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private MedalhaService medalhaService;

	public String inscrever(Long usuarioId, Long eventoId) {
	    if (repository.existsByUsuarioIdAndEventoId(usuarioId, eventoId)) {
	        return "Usuário já está inscrito nesse evento!";
	    }
	    Evento evento = eventoRepository.getReferenceById(eventoId);
	    Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
	    Participacao p = new Participacao();
	    p.setUsuario(usuario);
	    p.setEvento(evento);
	    p.setPontosGanhos(0);
	    p.setConfirmado(false);
	    repository.save(p);
	    return "Inscrição realizada com sucesso!";
	}

	public String participar(Long usuarioId, Long eventoId) {
	    if (!repository.existsByUsuarioIdAndEventoId(usuarioId, eventoId)) {
	        return "Usuário não está inscrito nesse evento!";
	    }
	    if (repository.existsByUsuarioIdAndEventoIdAndConfirmadoTrue(usuarioId, eventoId)) {
	        return "Usuário já confirmou participação nesse evento!";
	    }
	    Evento evento = eventoRepository.getReferenceById(eventoId);
	    Usuario usuario = usuarioRepository.getReferenceById(usuarioId);

	    Participacao p = repository.findByUsuarioIdAndEventoId(usuarioId, eventoId);
	    p.setConfirmado(true);
	    p.setPontosGanhos(evento.getPontos());
	    repository.save(p);

	    medalhaService.awardMedal(usuario, evento);

	    Integer total = repository.sumPontosByUsuarioId(usuario.getId());
	    medalhaService.awardMedalByPoints(usuario, total != null ? total : 0);

	    return "Participação confirmada!";
	}

	public List<Participacao> getAll() {
		return repository.findAllUsuarioEvento();
	}

	public Integer calcularTotalPontos(Long usuarioId) {
		Integer total = repository.sumPontosByUsuarioId(usuarioId);
		return total != null ? total : 0;
	}
}