package trab.lesw.competencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    @Query("SELECT c FROM Competencia c JOIN FETCH c.tag JOIN FETCH c.evento")
    List<Competencia> findAllDetalhado();

    List<Competencia> findByTagId(Long tagId);

    List<Competencia> findByEventoId(Long eventoId);
}
