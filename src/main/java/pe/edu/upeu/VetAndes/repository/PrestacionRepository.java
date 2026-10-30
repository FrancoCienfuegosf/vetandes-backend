package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.VetAndes.entity.Prestacion;

import java.util.List;

public interface PrestacionRepository extends JpaRepository<Prestacion, Long> {

    // RN-01: codigo unico (al registrar y al editar, excluyendo la propia)
    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    /*
     * Filtros opcionales: si un parametro llega null, su condicion no se
     * aplica. Con especie se devuelven las de esa especie y las de TODAS
     * (lo que necesita el formulario de atencion).
     */
    @Query("""
            select p
            from Prestacion p
            where (:especie is null
                   or p.especieAplicable = :especie
                   or p.especieAplicable = 'TODAS')
              and (:activo is null or p.activo = :activo)
            order by p.codigo
            """)
    List<Prestacion> buscar(@Param("especie") String especie,
                            @Param("activo") Boolean activo);
}
