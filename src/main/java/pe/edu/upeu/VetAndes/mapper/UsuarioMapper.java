package pe.edu.upeu.VetAndes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.dto.auth.UsuarioActualDTO;
import pe.edu.upeu.VetAndes.entity.Rol;
import pe.edu.upeu.VetAndes.entity.Usuario;
import pe.edu.upeu.VetAndes.security.PermisosPorRol;

import java.util.Collection;
import java.util.List;

@Component
public class UsuarioMapper {

    public UsuarioActualDTO toUsuarioActual(Usuario u) {
        return new UsuarioActualDTO(
                u.getId(), u.getUsername(), u.getNombreCompleto(), u.getActivo(),
                nombresRoles(u.getRoles()),
                PermisosPorRol.permisos(u.getRoles()));
    }

    public List<String> nombresRoles(Collection<Rol> roles) {
        return roles.stream().map(Rol::getNombre).sorted().toList();
    }
}
