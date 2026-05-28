package trab.lesw.participacao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {
    @Query("SELECT p FROM Participacao p JOIN FETCH p.usuario JOIN FETCH p.evento")
    List<Participacao> findAllUsuarioEvento();
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    @Query("SELECT COALESCE(SUM(p.pontosGanhos), 0) FROM Participacao p WHERE p.usuario.id = :usuarioId")
    Integer sumPontosByUsuarioId(Long usuarioId);
    List<Participacao> findByUsuarioId(Long usuarioId);

    @Query("SELECT p FROM Participacao p JOIN FETCH p.usuario WHERE p.evento.id = :eventoId")
    List<Participacao> findByEventoIdWithUsuario(Long eventoId);

    Participacao findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    boolean existsByUsuarioIdAndEventoIdAndConfirmadoTrue(Long usuarioId, Long eventoId);

    boolean existsByUsuarioIdAndEventoIdAndConfirmadoFalse(Long usuarioId, Long eventoId);

    @Query("SELECT p FROM Participacao p JOIN FETCH p.usuario WHERE p.evento.id = :eventoId AND p.confirmado = :confirmado")
    List<Participacao> findByEventoIdAndConfirmado(@Param("eventoId") Long eventoId, @Param("confirmado") Boolean confirmado);

    @Query("SELECT COUNT(p) FROM Participacao p WHERE p.evento.id = :eventoId AND p.confirmado = :confirmado")
    Long countByEventoIdAndConfirmado(@Param("eventoId") Long eventoId, @Param("confirmado") Boolean confirmado);
}