package pe.edu.upeu.VetAndes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

/*
 * Formato comun de error (seccion 3.5) para 400, 401, 403, 404, 409 y 500.
 * "campos" solo se incluye en los errores de validacion (NON_NULL).
 */
public record ErrorResponseDTO(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, String> campos
) {
}
