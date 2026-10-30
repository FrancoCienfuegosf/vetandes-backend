package pe.edu.upeu.VetAndes.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import pe.edu.upeu.VetAndes.dto.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Convierte las excepciones en respuestas con el formato comun (3.5).
 * Cada rechazo de una regla se registra con WARN; el error inesperado,
 * con ERROR y la traza completa (que no se envia al cliente).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* 400: errores de @Valid; "campos" lleva un mensaje por cada campo */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacion(MethodArgumentNotValidException ex,
                                                       HttpServletRequest request) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> campos.putIfAbsent(e.getField(), e.getDefaultMessage()));
        ex.getBindingResult().getGlobalErrors()
                .forEach(e -> campos.putIfAbsent(e.getObjectName(), e.getDefaultMessage()));
        log.warn("Validación rechazada en {} | campos={}", request.getRequestURI(), campos);
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", request, campos);
    }

    /* 400: regla que exige datos validos (RN-03 prestacion repetida, fechas...) */
    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<ErrorResponseDTO> solicitudInvalida(SolicitudInvalidaException ex,
                                                              HttpServletRequest request) {
        log.warn("Solicitud inválida en {} | {}", request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.BAD_REQUEST, ex.getMessage(), request, null);
    }

    /* 400: JSON mal formado o tipo incorrecto (por ejemplo "precio": "abc") */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> jsonIlegible(HttpMessageNotReadableException ex,
                                                         HttpServletRequest request) {
        log.warn("Cuerpo ilegible en {}", request.getRequestURI());
        return responder(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido o tiene tipos incorrectos", request, null);
    }

    /* 400: parametro de URL con tipo incorrecto (estado=XYZ, desde=hoy) */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> tipoParametro(MethodArgumentTypeMismatchException ex,
                                                          HttpServletRequest request) {
        String mensaje = "El valor '" + ex.getValue() + "' no es válido para el parámetro " + ex.getName();
        log.warn("{} en {}", mensaje, request.getRequestURI());
        return responder(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> parametroFaltante(MissingServletRequestParameterException ex,
                                                              HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST,
                "Falta el parámetro " + ex.getParameterName(), request, null);
    }

    /* 404: recurso inexistente */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> noEncontrado(RecursoNoEncontradoException ex,
                                                         HttpServletRequest request) {
        log.warn("No encontrado en {} | {}", request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /* 404: ruta que no existe */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDTO> rutaInexistente(NoResourceFoundException ex,
                                                            HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "La ruta solicitada no existe", request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDTO> metodoNoPermitido(HttpRequestMethodNotSupportedException ex,
                                                              HttpServletRequest request) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED,
                "El método " + ex.getMethod() + " no está permitido en esta ruta", request, null);
    }

    /* 409: regla de negocio (RN-01 a RN-05) */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> reglaNegocio(ReglaNegocioException ex,
                                                         HttpServletRequest request) {
        log.warn("Regla de negocio rechazada en {} | {}", request.getRequestURI(), ex.getMessage());
        return responder(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /*
     * 409: la base de datos rechazo un UNIQUE o una FK. Es la ultima
     * defensa si dos usuarios graban lo mismo al mismo tiempo.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> integridad(DataIntegrityViolationException ex,
                                                       HttpServletRequest request) {
        log.warn("Restricción de base de datos en {} | {}", request.getRequestURI(),
                ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT,
                "La operación viola una restricción de datos (registro duplicado o en uso)", request, null);
    }

    /*
     * 401 en el login: usuario inexistente, contrasena incorrecta o cuenta
     * inactiva responden el MISMO mensaje para no revelar cual fallo.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> autenticacion(AuthenticationException ex,
                                                          HttpServletRequest request) {
        log.warn("Autenticación fallida en {}", request.getRequestURI());
        return responder(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", request, null);
    }

    /* 403 lanzado por @PreAuthorize dentro del controlador (reporte) */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> accesoDenegado(AccessDeniedException ex,
                                                           HttpServletRequest request) {
        log.warn("Acceso denegado en {} {}", request.getMethod(), request.getRequestURI());
        return responder(HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta operación", request, null);
    }

    /* 500: error inesperado; el detalle tecnico va al log, no al cliente */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> inesperado(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {} {}", request.getMethod(), request.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Intente nuevamente", request, null);
    }

    private ResponseEntity<ErrorResponseDTO> responder(HttpStatus status, String mensaje,
                                                       HttpServletRequest request,
                                                       Map<String, String> campos) {
        ErrorResponseDTO cuerpo = new ErrorResponseDTO(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                campos);
        return ResponseEntity.status(status).body(cuerpo);
    }
}
