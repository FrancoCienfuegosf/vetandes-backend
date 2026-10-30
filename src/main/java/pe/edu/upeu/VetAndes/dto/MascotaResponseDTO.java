package pe.edu.upeu.VetAndes.dto;

import java.time.LocalDate;

/*
 * Devuelve propietarioId y propietarioNombre, nunca la entidad Propietario
 * anidada: el JSON no depende de la estructura de las tablas, no hay
 * referencias circulares ni carga diferida fuera de la transaccion.
 */
public record MascotaResponseDTO(
        Long id,
        String nombre,
        String especie,
        String raza,
        LocalDate fechaNacimiento,
        String sexo,
        Long propietarioId,
        String propietarioNombre
) {
}
