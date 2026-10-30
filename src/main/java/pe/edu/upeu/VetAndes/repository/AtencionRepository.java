package pe.edu.upeu.VetAndes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.VetAndes.dto.AtencionesPorEspecieDTO;
import pe.edu.upeu.VetAndes.entity.Atencion;
import pe.edu.upeu.VetAndes.entity.EstadoAtencion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AtencionRepository extends JpaRepository<Atencion, Long> {

    // RN-02: una mascota con atenciones no se elimina
    boolean existsByMascotaId(Long mascotaId);

    // Ultimo numero emitido (AT-000004). Como todos tienen 9 caracteres,
    // el maximo alfabetico coincide con el maximo numerico.
    @Query("select max(a.numero) from Atencion a")
    String ultimoNumero();

    /* Una atencion con su mascota, propietario, detalles y prestaciones. */
    @Query("""
            select a
            from Atencion a
            join fetch a.mascota m
            join fetch m.propietario
            left join fetch a.detalles d
            left join fetch d.prestacion
            where a.id = :id
            """)
    Optional<Atencion> buscarCompleta(@Param("id") Long id);

    /*
     * Consulta con filtros combinables y opcionales. Si un parametro es
     * null su condicion se ignora. mascota llega ya en minusculas y con
     * comodines (%texto%) desde el servicio: busqueda parcial sin
     * distinguir mayusculas.
     */
    @Query("""
            select a
            from Atencion a
            join fetch a.mascota m
            join fetch m.propietario
            left join fetch a.detalles d
            left join fetch d.prestacion
            where (:mascota is null or lower(m.nombre) like :mascota)
              and (:especie is null or m.especie = :especie)
              and (:estado  is null or a.estado = :estado)
              and (:desde   is null or a.fecha >= :desde)
              and (:hasta   is null or a.fecha <= :hasta)
            order by a.fecha desc
            """)
    List<Atencion> buscar(@Param("mascota") String mascota,
                          @Param("especie") String especie,
                          @Param("estado") EstadoAtencion estado,
                          @Param("desde") LocalDateTime desde,
                          @Param("hasta") LocalDateTime hasta);

    /*
     * Reporte agregado: la base de datos agrupa y suma (GROUP BY) y
     * devuelve una fila por especie, proyectada directamente al DTO.
     * No se usa findAll() ni bucles en Java: viajan 2 o 3 filas, no
     * todas las atenciones. Se excluyen las ANULADAS.
     */
    @Query("""
            select new pe.edu.upeu.VetAndes.dto.AtencionesPorEspecieDTO(
                       m.especie,
                       count(a),
                       sum(a.total))
            from Atencion a
            join a.mascota m
            where a.estado <> pe.edu.upeu.VetAndes.entity.EstadoAtencion.ANULADA
              and (:desde is null or a.fecha >= :desde)
              and (:hasta is null or a.fecha <= :hasta)
            group by m.especie
            order by sum(a.total) desc
            """)
    List<AtencionesPorEspecieDTO> reportePorEspecie(@Param("desde") LocalDateTime desde,
                                                    @Param("hasta") LocalDateTime hasta);
}
