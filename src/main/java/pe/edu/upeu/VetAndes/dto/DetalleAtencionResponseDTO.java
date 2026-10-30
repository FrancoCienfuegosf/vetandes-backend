package pe.edu.upeu.VetAndes.dto;

import java.math.BigDecimal;

public record DetalleAtencionResponseDTO(
        Long id,
        Long prestacionId,
        String prestacionCodigo,
        String prestacionNombre,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
