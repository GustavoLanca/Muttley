package trab.lesw.usuario;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
	List<Usuario> findByTipo(String tipo);
	Optional<Usuario> findByCpf(String cpf);
	Optional<Usuario> findByEmail(String email);
}
