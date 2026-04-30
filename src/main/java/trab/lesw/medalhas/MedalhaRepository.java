package trab.lesw.medalhas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {

    @Query("SELECT m FROM Medalha m WHERE m.usuario IS NULL OR m.usuario.id != :usuarioId")
    List<Medalha> findMedalhasDisponiveisParaUsuario(@Param("usuarioId") Long usuarioId);

    List<Medalha> findByUsuarioId(Long usuarioId);
}