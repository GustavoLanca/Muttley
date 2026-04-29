package trab.lesw.medalhas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MedalhaService {

    @Autowired
    private MedalhaRepository repository;

    public List<Medalha> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Medalha getById(Long id) {
        return repository.getReferenceById(id);
    }

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
}