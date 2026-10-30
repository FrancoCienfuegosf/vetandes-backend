package pe.edu.upeu.VetAndes.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pe.edu.upeu.VetAndes.entity.Rol;

import java.util.*;

import static pe.edu.upeu.VetAndes.security.Permiso.*;

/*
 * Matriz de roles y permisos (seccion 3.2), en un solo lugar.
 *
 * Los roles estan en la tabla roles; sus permisos, aqui. Un usuario con
 * varios roles recibe la UNION de sus permisos. Un rol que no figura en el
 * mapa no otorga ningun permiso.
 */
public final class PermisosPorRol {

    private static final Map<String, Set<Permiso>> PERMISOS = Map.of(
            "ADMIN", Collections.unmodifiableSet(EnumSet.allOf(Permiso.class)),
            "RECEPCIONISTA", Collections.unmodifiableSet(EnumSet.of(
                    PRESTACION_LEER,
                    MASCOTA_LEER,
                    MASCOTA_ESCRIBIR,
                    ATENCION_LEER,
                    ATENCION_REGISTRAR)),
            "VETERINARIO", Collections.unmodifiableSet(EnumSet.of(
                    PRESTACION_LEER,
                    PRESTACION_ESCRIBIR,
                    MASCOTA_LEER,
                    ATENCION_LEER,
                    ATENCION_ANULAR,
                    REPORTE_VER))
    );

    private PermisosPorRol() {
    }

    public static Set<Permiso> de(String nombreRol) {
        return PERMISOS.getOrDefault(nombreRol, Set.of());
    }

    /* Union de los permisos de todos los roles, ordenada alfabeticamente */
    public static List<String> permisos(Collection<Rol> roles) {
        Set<String> nombres = new TreeSet<>();
        roles.forEach(rol -> de(rol.getNombre()).forEach(p -> nombres.add(p.name())));
        return new ArrayList<>(nombres);
    }

    /*
     * Authorities para Spring Security: un "ROLE_<rol>" por cada rol
     * (informativo) mas un authority por cada permiso (el que se exige).
     */
    public static List<GrantedAuthority> authorities(Collection<Rol> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        roles.stream().map(Rol::getNombre).sorted()
                .forEach(nombre -> authorities.add(new SimpleGrantedAuthority("ROLE_" + nombre)));
        permisos(roles).forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        return authorities;
    }
}
