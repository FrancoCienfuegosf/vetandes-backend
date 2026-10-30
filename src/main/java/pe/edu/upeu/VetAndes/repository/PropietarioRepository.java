package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.VetAndes.entity.Propietario;

import java.util.List;

public interface PropietarioRepository extends JpaRepository<Propietario, Long> {

    List<Propietario> findAllByOrderByNombresAsc();
}
