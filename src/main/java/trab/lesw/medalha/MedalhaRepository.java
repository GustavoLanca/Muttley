package trab.lesw.medalha;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {
    List<Medalha> findByUsuarioId(Long usuarioId);
    long countByUsuarioIdAndNome(Long usuarioId, String nome);
    List<Medalha> findByEventoId(Long eventoId);

    @Procedure(procedureName = "sp_contar_medalhas_usuario")
    Long contarMedalhasUsuarioProcedure(@Param("p_usuario_id") Long usuarioId, @Param("p_nome") String nome);
}
