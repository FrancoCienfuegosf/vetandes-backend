package pe.edu.upeu.VetAndes.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.config.JwtAuthenticationFilter;

import java.io.IOException;

/*
 * 401 Unauthorized: la peticion llego a una ruta protegida SIN usuario
 * autenticado (no envio token, el token esta alterado o vencido).
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(RestAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        Object motivo = request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_ERROR);
        String mensaje = motivo != null
                ? motivo.toString()
                : "Debe iniciar sesión para acceder a este recurso";
        log.warn("401 en {} {} | {}", request.getMethod(), request.getRequestURI(), mensaje);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        RespuestaErrorSeguridad.escribir(request, response,
                HttpStatus.UNAUTHORIZED.value(), "Unauthorized", mensaje);
    }
}
