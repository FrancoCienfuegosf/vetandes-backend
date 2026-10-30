package pe.edu.upeu.VetAndes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AtencionResponseDTO(
        Long id,
        String numero,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        Long mascotaId,
        String mascotaNombre,
        String especie,
        Long propietarioId,
        String propietarioNombre,
        BigDecimal peso,
        String diagnostico,
        String estado,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        String usuarioRegistro,
        String motivoAnulacion,
        List<DetalleAtencionResponseDTO> detalles
) {
}
