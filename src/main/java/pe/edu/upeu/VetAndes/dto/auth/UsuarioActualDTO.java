package pe.edu.upeu.VetAndes.dto.auth;

import java.util.List;

/* Respuesta de GET /auth/me: el usuario del token, sin contrasena */
public record UsuarioActualDTO(
        Long id,
        String username,
        String nombreCompleto,
        Boolean activo,
        List<String> roles,
        List<String> permisos
) {
}
