package trab.lesw.aluno;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    @Query("SELECT DISTINCT a FROM Aluno a LEFT JOIN FETCH a.usuario u LEFT JOIN FETCH u.participacoes")
    List<Aluno> findAllWithUsuario();
}
