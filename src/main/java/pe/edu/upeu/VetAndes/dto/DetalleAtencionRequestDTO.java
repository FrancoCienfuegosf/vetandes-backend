package pe.edu.upeu.VetAndes.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Solo prestacion y cantidad: el precio lo pone el servidor desde la BD */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAtencionRequestDTO {

    @NotNull(message = "La prestación es obligatoria")
    @Positive(message = "El identificador de la prestación debe ser válido")
    private Long prestacionId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Max(value = 3, message = "La cantidad máxima es 3")
    private Integer cantidad;
}
