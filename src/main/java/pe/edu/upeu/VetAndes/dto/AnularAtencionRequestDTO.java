package pe.edu.upeu.VetAndes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnularAtencionRequestDTO {

    // RN-05: motivo obligatorio de 10 a 200 caracteres (400)
    @NotBlank(message = "El motivo de anulación es obligatorio")
    @Size(min = 10, max = 200, message = "El motivo debe tener entre 10 y 200 caracteres")
    private String motivo;
}
