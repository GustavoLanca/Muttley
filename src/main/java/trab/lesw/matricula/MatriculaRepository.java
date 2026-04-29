package trab.lesw.matricula;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @Query("SELECT m FROM Matricula m JOIN FETCH m.aluno JOIN FETCH m.disciplina")
    List<Matricula> findAllAlunoDisciplina();

    List<Matricula> findByAlunoId(Long alunoId);

    List<Matricula> findByDisciplinaId(Long disciplinaId);
}
