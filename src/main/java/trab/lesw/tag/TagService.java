package trab.lesw.tag;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
@Service
public class TagService {
 
    @Autowired
    private TagRepository repository;
 
    public List<Tag> getAll() {
        return repository.findAll();
    }
 
    public Tag getById(Long id) {
        return repository.getReferenceById(id);
    }
 
    public String save(Tag tag) {
        boolean isNovo = tag.getId() == null;
        repository.save(tag);
        return isNovo ? "Tag criada" : "Tag atualizada";
    }
 
    public String delete(Long id) {
        try {
            repository.deleteById(id);
            return "Tag deletada";
        } catch (Exception e) {
            return "Não é possível excluir, está em uso";
        }
    }
}