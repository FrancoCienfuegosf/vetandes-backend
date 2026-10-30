package pe.edu.upeu.VetAndes.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.VetAndes.dto.auth.LoginRequestDTO;
import pe.edu.upeu.VetAndes.dto.auth.LoginResponseDTO;
import pe.edu.upeu.VetAndes.dto.auth.UsuarioActualDTO;
import pe.edu.upeu.VetAndes.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /* Publico. 200 con el token; 400 si faltan datos; 401 si no coinciden */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    /* Autenticado. Spring inyecta la Authentication que dejo el filtro JWT */
    @GetMapping("/me")
    public ResponseEntity<UsuarioActualDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.usuarioActual(authentication.getName()));
    }
}
