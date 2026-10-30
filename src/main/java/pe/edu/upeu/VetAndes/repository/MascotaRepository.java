package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.VetAndes.entity.Mascota;

import java.util.List;
import java.util.Optional;

/*
 * @EntityGraph(attributePaths = "propietario") trae al propietario en el
 * mismo SELECT (JOIN). Sin esto, la lista haria un SELECT extra por cada
 * mascota al leer el nombre del propietario (problema N+1).
 */
public interface MascotaRepository extends JpaRepository<Mascota, Long> {

    @EntityGraph(attributePaths = "propietario")
    List<Mascota> findAllByOrderByNombreAsc();

    @EntityGraph(attributePaths = "propietario")
    List<Mascota> findByPropietarioIdOrderByNombreAsc(Long propietarioId);

    @EntityGraph(attributePaths = "propietario")
    Optional<Mascota> findWithPropietarioById(Long id);

    // RN-02: un propietario no puede tener dos mascotas con el mismo nombre
    boolean existsByNombreIgnoreCaseAndPropietarioId(String nombre, Long propietarioId);

    boolean existsByNombreIgnoreCaseAndPropietarioIdAndIdNot(String nombre, Long propietarioId, Long id);
}
