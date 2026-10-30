package pe.edu.upeu.VetAndes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MascotaRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 40, message = "El nombre debe tener entre 2 y 40 caracteres")
    private String nombre;

    @NotBlank(message = "La especie es obligatoria")
    @Pattern(regexp = "^(PERRO|GATO|OTRO)$", message = "La especie debe ser PERRO, GATO u OTRO")
    private String especie;

    @Size(max = 40, message = "La raza admite hasta 40 caracteres")
    private String raza;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @PastOrPresent(message = "La fecha de nacimiento no puede ser futura")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El sexo es obligatorio")
    @Pattern(regexp = "^[MH]$", message = "El sexo debe ser M o H")
    private String sexo;

    @NotNull(message = "El propietario es obligatorio")
    @Positive(message = "El identificador del propietario debe ser válido")
    private Long propietarioId;
}
