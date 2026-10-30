package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.VetAndes.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);
}
