package trab.lesw.participacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public String participar(Long usuarioId, Long eventoId) {
		if (repository.existsByUsuarioIdAndEventoId(usuarioId, eventoId)) {
			return "Usuário já está inscrito nesse evento!";
		}
		Evento evento = eventoRepository.getReferenceById(eventoId);
		Participacao p = new Participacao();
		p.setUsuario(usuarioRepository.getReferenceById(usuarioId));
		p.setEvento(evento);
		p.setPontosObtidos(evento.getPontos());
		repository.save(p);

		return "Participação registrada!";
	}

	public List<Participacao> getAll() {
		return repository.findAllUsuarioEvento();
	}

	public Integer calcularTotalPontos(Long usuarioId) {
		Integer total = repository.sumPontosByUsuarioId(usuarioId);
		return total != null ? total : 0;
	}
}