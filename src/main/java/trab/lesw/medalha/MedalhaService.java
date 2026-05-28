package trab.lesw.medalha;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import trab.lesw.evento.Evento;
import trab.lesw.tag.Tag;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final String MEDALHA_PONTOS = "medalha por participação";

    public List<Medalha> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Medalha getById(Long id) {
        return repository.getReferenceById(id);
    }

    public String save(Medalha medalha) {
        boolean isNovo = medalha.getId() == null;

        if (!MEDALHA_PONTOS.equals(medalha.getNome())
                && repository.countByUsuarioIdAndNome(medalha.getUsuario().getId(), medalha.getNome()) > 0) {
            return "Já existe uma medalha com este nome para este usuário!";
        }

        repository.save(medalha);
        return isNovo ? "Medalha criada com sucesso!" : "Medalha atualizada com sucesso!";
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Medalha deletada!";
    }

    public void awardMedal(Usuario usuario, Evento evento) {
        List<Tag> tags = evento.getTags();
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (Tag tag : tags) {
            if (repository.countByUsuarioIdAndNome(usuario.getId(), tag.getNome()) > 0) {
                continue;
            }
            Medalha medalha = new Medalha();
            medalha.setNome(tag.getNome());
            medalha.setUsuario(usuario);
            medalha.setEvento(evento);
            repository.save(medalha);
        }
    }

    public void awardMedalByPoints(Usuario usuario, int totalPontos) {
        int medalsEsperados = totalPontos / 10;
        long medalsAtuais = repository.countByUsuarioIdAndNome(usuario.getId(), MEDALHA_PONTOS);

        for (long i = medalsAtuais; i < medalsEsperados; i++) {
            Medalha medalha = new Medalha();
            medalha.setNome(MEDALHA_PONTOS);
            medalha.setUsuario(usuario);
            medalha.setEvento(null);
            repository.save(medalha);
        }
    }

    public List<Medalha> getMedalhasByUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId);
    }
}
