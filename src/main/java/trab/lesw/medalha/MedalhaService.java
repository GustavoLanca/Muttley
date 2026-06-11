package trab.lesw.medalha;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import trab.lesw.evento.Evento;
import trab.lesw.participacao.ParticipacaoRepository;
import trab.lesw.tag.Tag;
import trab.lesw.usuario.Usuario;
import trab.lesw.usuario.UsuarioRepository;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ParticipacaoRepository participacaoRepository;

    private static final String MEDALHA_PONTOS = "medalha por participacao";

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

    public List<Map<String, Object>> getRanking() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Usuario u : usuarios) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("usuario", u);
            entry.put("totalPontos", participacaoRepository.sumPontosByUsuarioId(u.getId()));
            entry.put("qtdMedalhas", repository.countByUsuarioId(u.getId()));
            entry.put("medalhas", repository.findByUsuarioId(u.getId()));
            ranking.add(entry);
        }
        ranking.sort(Comparator.comparingInt(
            (Map<String, Object> m) -> (Integer) m.get("totalPontos")).reversed());
        return ranking;
    }

    public Map<String, Object> entregarSelecionadas(List<Long> ids) {
        Map<String, Object> report = new LinkedHashMap<>();
        List<Map<String, Object>> itens = new ArrayList<>();
        Map<String, Long> resumo = new LinkedHashMap<>();

        if (ids != null) {
            for (Long id : ids) {
                repository.findById(id).ifPresent(m -> {
                    m.setEntregue(true);
                    repository.save(m);

                    String nome = m.getUsuario().getNome();
                    String medalhaNome = m.getNome();

                    resumo.merge(medalhaNome, 1L, Long::sum);

                    Map<String, Object> item = itens.stream()
                        .filter(i -> i.get("usuarioNome").equals(nome))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Object> novo = new LinkedHashMap<>();
                            novo.put("usuarioNome", nome);
                            novo.put("medalhas", new ArrayList<String>());
                            itens.add(novo);
                            return novo;
                        });
                    @SuppressWarnings("unchecked")
                    List<String> medalhas = (List<String>) item.get("medalhas");
                    medalhas.add(medalhaNome);
                });
            }
        }

        report.put("itens", itens);
        report.put("resumo", resumo);
        return report;
    }

    public void reverterEntrega(Long medalhaId) {
        repository.findById(medalhaId).ifPresent(m -> {
            m.setEntregue(false);
            repository.save(m);
        });
    }
}
