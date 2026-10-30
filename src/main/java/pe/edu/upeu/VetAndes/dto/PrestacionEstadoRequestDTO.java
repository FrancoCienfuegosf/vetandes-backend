package pe.edu.upeu.VetAndes.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/* Cuerpo de PATCH /prestaciones/{id}/estado: { "activo": false } da de baja */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestacionEstadoRequestDTO {

    @NotNull(message = "El estado (activo) es obligatorio")
    private Boolean activo;
}
