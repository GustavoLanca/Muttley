package trab.lesw.usuario;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.participacoes p LEFT JOIN FETCH p.evento LEFT JOIN FETCH u.medalhas WHERE u.id = :id")
    Usuario findByIdWithParticipacoesAndMedalhas(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.medalhas WHERE u.id = :id")
    Usuario findByIdWithMedalhas(@Param("id") Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.medalhas ORDER BY u.nome ASC")
    List<Usuario> findAllWithMedalhas();
}
