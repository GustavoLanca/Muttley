package trab.lesw.forum;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ForumRepository extends JpaRepository<Forum, Long> {

    @Query("SELECT f FROM Forum f JOIN FETCH f.aluno JOIN FETCH f.evento JOIN FETCH f.disciplina")
    List<Forum> findAllDetalhado();

    List<Forum> findByAlunoId(Long alunoId);

    List<Forum> findByEventoId(Long eventoId);

    List<Forum> findByDisciplinaId(Long disciplinaId);
}
