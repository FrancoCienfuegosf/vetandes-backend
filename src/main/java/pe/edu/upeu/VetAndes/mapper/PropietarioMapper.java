package pe.edu.upeu.VetAndes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.dto.PropietarioResponseDTO;
import pe.edu.upeu.VetAndes.entity.Propietario;

@Component
public class PropietarioMapper {

    public PropietarioResponseDTO toResponse(Propietario p) {
        return new PropietarioResponseDTO(p.getId(), p.getDni(), p.getNombres(), p.getTelefono());
    }
}
