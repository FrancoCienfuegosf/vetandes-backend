package pe.edu.upeu.VetAndes.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/*
 * Escribe los 401 y 403 con el MISMO formato que ErrorResponseDTO.
 *
 * Hace falta porque estos errores ocurren en los filtros de seguridad,
 * ANTES de llegar a los controladores, y alli @RestControllerAdvice no
 * interviene.
 */
final class RespuestaErrorSeguridad {

    private RespuestaErrorSeguridad() {
    }

    static void escribir(HttpServletRequest request, HttpServletResponse response,
                         int status, String error, String mensaje) throws IOException {
        String json = "{"
                + "\"timestamp\":\"" + LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS) + "\","
                + "\"status\":" + status + ","
                + "\"error\":\"" + escapar(error) + "\","
                + "\"mensaje\":\"" + escapar(mensaje) + "\","
                + "\"ruta\":\"" + escapar(request.getRequestURI()) + "\""
                + "}";
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }

    private static String escapar(String texto) {
        return texto == null ? "" : texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
