package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.upeu.VetAndes.entity.DetalleAtencion;

public interface DetalleAtencionRepository extends JpaRepository<DetalleAtencion, Long> {

    // RN-01: una prestacion usada en alguna atencion no se elimina fisicamente
    boolean existsByPrestacionId(Long prestacionId);
}
