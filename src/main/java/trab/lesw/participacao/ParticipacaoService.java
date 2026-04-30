package trab.lesw.participacao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;
import trab.lesw.evento.Evento;
import trab.lesw.evento.EventoRepository;
import trab.lesw.medalhas.Medalha;
import trab.lesw.medalhas.MedalhaRepository;
import trab.lesw.evento.TipoEvento;

@Service
public class ParticipacaoService {

    @Autowired
    private ParticipacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private MedalhaRepository medalhaRepository;

    public String participar(Long usuarioId, Long eventoId) {
        var usuarioOpt = usuarioRepository.findById(usuarioId);
        var eventoOpt = eventoRepository.findById(eventoId);
        if (usuarioOpt.isEmpty()) {
            return "Erro: Usuário não encontrado!";
        }
        if (eventoOpt.isEmpty()) {
            return "Erro: Evento não encontrado!";
        }
        Participacao p = new Participacao();
        p.setUsuario(usuarioOpt.get());
        p.setEvento(eventoOpt.get());

        Evento evento = eventoOpt.get();
        int pontos = evento.getTipoEvento() != null ? evento.getTipoEvento().getPontos() : TipoEvento.OUTRO.getPontos();
        p.setPontos(pontos);

        repository.save(p);

        // Verificar e atribuir medalhas
        atribuirMedalhas(usuarioOpt.get());

        return "Participação registrada! Pontos: " + pontos;
    }

    private void atribuirMedalhas(Usuario usuario) {
        // Recarregar usuário com participações e medalhas
        Usuario usuarioCompleto = usuarioRepository.findByIdWithParticipacoesAndMedalhas(usuario.getId());
        int totalPontos = usuarioCompleto.getTotalPontos();

        // Buscar medalhas que o usuário ainda não tem
        List<Medalha> medalhasDisponiveis = medalhaRepository.findAll();
        List<Medalha> medalhasUsuario = usuarioCompleto.getMedalhas();

        for (Medalha medalha : medalhasDisponiveis) {
            boolean jaTem = medalhasUsuario.stream()
                    .anyMatch(m -> m.getId().equals(medalha.getId()));
            if (!jaTem && totalPontos >= medalha.getPontosNecessarios()) {
                medalha.setUsuario(usuarioCompleto);
                medalhaRepository.save(medalha);
            }
        }
    }

    public List<Participacao> getAll() {
        return repository.findAllUsuarioEvento();
    }

}