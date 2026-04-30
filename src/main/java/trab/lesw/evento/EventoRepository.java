package trab.lesw.evento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query("SELECT e FROM Evento e LEFT JOIN FETCH e.competencias")
    List<Evento> findAllWithCompetencias();

    @Query("SELECT e FROM Evento e WHERE e.tipoEvento IS NULL")
    List<Evento> findEventosSemTipo();
}
