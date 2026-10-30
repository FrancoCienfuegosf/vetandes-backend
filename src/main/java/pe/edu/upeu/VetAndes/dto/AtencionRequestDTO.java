package pe.edu.upeu.VetAndes.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/*
 * No tiene subtotal, descuento ni total: si el cliente los envia,
 * ignoreUnknown = true hace que Jackson los descarte sin error. Los calcula
 * AtencionServiceImpl (RN-04).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AtencionRequestDTO {

    @NotNull(message = "La mascota es obligatoria")
    @Positive(message = "El identificador de la mascota debe ser válido")
    private Long mascotaId;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    @Digits(integer = 3, fraction = 2, message = "El peso admite hasta 3 enteros y 2 decimales")
    private BigDecimal peso;

    @Size(max = 300, message = "El diagnóstico admite hasta 300 caracteres")
    private String diagnostico;

    @NotNull(message = "La atención debe tener detalles")
    @Size(min = 1, max = 8, message = "La atención debe tener entre 1 y 8 detalles")
    @Valid
    private List<DetalleAtencionRequestDTO> detalles;
}
