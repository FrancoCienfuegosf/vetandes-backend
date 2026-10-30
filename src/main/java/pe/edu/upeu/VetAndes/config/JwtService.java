package pe.edu.upeu.VetAndes.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/*
 * Genera y valida los JWT.
 *
 * El token se FIRMA con HMAC-SHA usando un secreto que solo conoce el
 * servidor: si alguien cambia un caracter, la firma deja de coincidir.
 * El payload NO esta cifrado (es Base64): por eso no lleva la contrasena.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMinutos;

    public JwtService(@Value("${app.jwt.secret}") String secretoBase64,
                      @Value("${app.jwt.expiracion-minutos:60}") long expiracionMinutos) {
        // Minimo 256 bits; si es mas corta, hmacShaKeyFor falla al arrancar
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.expiracionMinutos = expiracionMinutos;
    }

    /* sub = username; ademas nombre, roles, permisos, iat (emitido) y exp (vence) */
    public String generarToken(String username, String nombreCompleto,
                               List<String> roles, List<String> permisos) {
        Instant ahora = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("nombreCompleto", nombreCompleto)
                .claim("roles", roles)
                .claim("permisos", permisos)
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(ahora.plus(expiracionMinutos, ChronoUnit.MINUTES)))
                .signWith(clave)
                .compact();
    }

    /*
     * Valida firma y expiracion y devuelve el username. Lanza
     * ExpiredJwtException si vencio y JwtException si fue alterado.
     */
    public String extraerUsername(String token) {
        return leerClaims(token).getSubject();
    }

    public Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpiracionSegundos() {
        return expiracionMinutos * 60;
    }
}
