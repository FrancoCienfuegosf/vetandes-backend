package pe.edu.upeu.VetAndes.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestacionRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^PRS-\\d{3}$", message = "El código debe tener el formato PRS-000")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 2 decimales")
    private BigDecimal precio;

    @NotBlank(message = "La especie aplicable es obligatoria")
    @Pattern(regexp = "^(PERRO|GATO|TODAS)$", message = "La especie aplicable debe ser PERRO, GATO o TODAS")
    private String especieAplicable;

    /* Opcional: al registrar, si no llega, la prestacion nace activa */
    private Boolean activo;
}
