package pe.edu.upeu.VetAndes.dto.auth;

import java.util.List;

/*
 * Respuesta del login. Nunca incluye la contrasena. La SPA guarda el token
 * y lo envia como: Authorization: Bearer <token>
 */
public record LoginResponseDTO(
        String token,
        String tipo,
        long expiraEnSegundos,
        String username,
        String nombreCompleto,
        List<String> roles,
        List<String> permisos
) {
}
