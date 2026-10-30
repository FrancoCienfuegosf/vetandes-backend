package pe.edu.upeu.VetAndes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.config.JwtService;
import pe.edu.upeu.VetAndes.dto.auth.LoginRequestDTO;
import pe.edu.upeu.VetAndes.dto.auth.LoginResponseDTO;
import pe.edu.upeu.VetAndes.dto.auth.UsuarioActualDTO;
import pe.edu.upeu.VetAndes.entity.Usuario;
import pe.edu.upeu.VetAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.VetAndes.mapper.UsuarioMapper;
import pe.edu.upeu.VetAndes.repository.UsuarioRepository;
import pe.edu.upeu.VetAndes.security.PermisosPorRol;
import pe.edu.upeu.VetAndes.service.AuthService;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UsuarioMapper usuarioMapper;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           UsuarioMapper usuarioMapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.usuarioMapper = usuarioMapper;
    }

    /*
     * 1. Busca al usuario.
     * 2. Compara la contrasena enviada con el hash BCrypt guardado usando
     *    passwordEncoder.matches (BCrypt no se "descifra": vuelve a calcular
     *    el hash con la sal guardada y compara).
     * 3. Usuario inexistente, contrasena incorrecta o cuenta inactiva lanzan
     *    la MISMA excepcion -> 401 con el mismo mensaje.
     * La contrasena nunca se escribe en el log ni se devuelve.
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        String username = request.getUsername().trim();
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username).orElse(null);

        boolean valido = usuario != null
                && passwordEncoder.matches(request.getPassword(), usuario.getPassword())
                && Boolean.TRUE.equals(usuario.getActivo());
        if (!valido) {
            log.warn("Inicio de sesión rechazado | usuario={}", username);
            throw new BadCredentialsException("Usuario o contraseña incorrectos");
        }

        List<String> roles = usuarioMapper.nombresRoles(usuario.getRoles());
        List<String> permisos = PermisosPorRol.permisos(usuario.getRoles());
        String token = jwtService.generarToken(usuario.getUsername(), usuario.getNombreCompleto(), roles, permisos);

        log.info("Inicio de sesión correcto | usuario={} | roles={}", usuario.getUsername(), roles);
        return new LoginResponseDTO(token, "Bearer", jwtService.getExpiracionSegundos(),
                usuario.getUsername(), usuario.getNombreCompleto(), roles, permisos);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioActualDTO usuarioActual(String username) {
        Usuario usuario = usuarioRepository.findByUsernameIgnoreCase(username).orElseThrow(() ->
                new RecursoNoEncontradoException("Usuario no encontrado: " + username));
        return usuarioMapper.toUsuarioActual(usuario);
    }
}
