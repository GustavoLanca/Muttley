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
}
