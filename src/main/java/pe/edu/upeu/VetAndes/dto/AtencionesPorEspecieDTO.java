package pe.edu.upeu.VetAndes.dto;

import java.math.BigDecimal;

/*
 * Proyeccion del reporte: la crea JPQL con "select new ..." directamente
 * desde el resultado del GROUP BY (ver AtencionRepository).
 */
public record AtencionesPorEspecieDTO(
        String especie,
        Long cantidadAtenciones,
        BigDecimal importeTotal
) {
}
