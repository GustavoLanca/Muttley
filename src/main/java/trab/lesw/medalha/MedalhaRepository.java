package trab.lesw.medalha;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface MedalhaRepository extends JpaRepository<Medalha, Long> {
    List<Medalha> findByUsuarioId(Long usuarioId);
    long countByUsuarioIdAndNome(Long usuarioId, String nome);
}
