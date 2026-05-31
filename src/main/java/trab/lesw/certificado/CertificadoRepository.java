package trab.lesw.certificado;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    List<Certificado> findByUsuarioId(Long usuarioId);

    @Query("SELECT c FROM Certificado c JOIN FETCH c.evento WHERE c.usuario.id = :usuarioId")
    List<Certificado> findByUsuarioIdWithEvento(@Param("usuarioId") Long usuarioId);

    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    Optional<Certificado> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);

    @Procedure(procedureName = "sp_existe_certificado")
    Boolean existeCertificadoProcedure(@Param("p_usuario_id") Long usuarioId, @Param("p_evento_id") Long eventoId);
}
