package trab.lesw.medalhas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import trab.lesw.usuario.UsuarioRepository;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Medalha> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Medalha getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Transactional
    public String save(Medalha medalha) {
        repository.save(medalha);
        return medalha.getId() != null ?
                "Medalha atualizada com sucesso!" :
                "Medalha criada com sucesso!";
    }

    public String delete(Long id) {
        repository.deleteById(id);
        return "Medalha deletada!";
    }

    @Transactional
    public String atribuirMedalhaAUsuario(Long medalhaId, Long usuarioId) {
        var medalhaOpt = repository.findById(medalhaId);
        var usuarioOpt = usuarioRepository.findById(usuarioId);

        if (medalhaOpt.isEmpty()) {
            return "Erro: Medalha não encontrada!";
        }
        if (usuarioOpt.isEmpty()) {
            return "Erro: Usuário não encontrado!";
        }

        Medalha medalha = medalhaOpt.get();
        medalha.setUsuario(usuarioOpt.get());
        repository.save(medalha);

        return "Medalha atribuída com sucesso!";
    }
}