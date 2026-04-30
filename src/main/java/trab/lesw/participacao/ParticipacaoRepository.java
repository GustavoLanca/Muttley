package trab.lesw.participacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {
    @Query("SELECT DISTINCT p FROM Participacao p LEFT JOIN FETCH p.usuario LEFT JOIN FETCH p.evento")
    List<Participacao> findAllUsuarioEvento();
}