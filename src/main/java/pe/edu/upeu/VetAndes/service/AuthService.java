package pe.edu.upeu.VetAndes.service;

import pe.edu.upeu.VetAndes.dto.auth.LoginRequestDTO;
import pe.edu.upeu.VetAndes.dto.auth.LoginResponseDTO;
import pe.edu.upeu.VetAndes.dto.auth.UsuarioActualDTO;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
    UsuarioActualDTO usuarioActual(String username);
}
