package trab.lesw.tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class TagService {

    @Autowired
    private TagRepository repository;

    public List<Tag> getAll() {
        return repository.findAll(Sort.by("nome").ascending());
    }

    public Tag getById(Long id) {
        return repository.getReferenceById(id);
    }

    @Transactional
    public String save(Tag tag) {
        repository.save(tag);
        return tag.getId() != null ?
                "Tag atualizada com sucesso!" :
                "Tag criada com sucesso!";
    }

    @Transactional
    public String delete(Long id) {
        repository.deleteById(id);
        return "Tag deletada!";
    }
}
