package pe.edu.upeu.VetAndes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.dto.MascotaRequestDTO;
import pe.edu.upeu.VetAndes.dto.MascotaResponseDTO;
import pe.edu.upeu.VetAndes.entity.Mascota;
import pe.edu.upeu.VetAndes.entity.Propietario;

@Component
public class MascotaMapper {

    /* Aplana la relacion: solo id y nombre del propietario */
    public MascotaResponseDTO toResponse(Mascota m) {
        return new MascotaResponseDTO(
                m.getId(), m.getNombre(), m.getEspecie(), m.getRaza(),
                m.getFechaNacimiento(), m.getSexo(),
                m.getPropietario().getId(), m.getPropietario().getNombres());
    }

    public void copiar(MascotaRequestDTO dto, Mascota m, Propietario propietario) {
        m.setNombre(dto.getNombre().trim());
        m.setEspecie(dto.getEspecie());
        m.setRaza(dto.getRaza() == null || dto.getRaza().isBlank() ? null : dto.getRaza().trim());
        m.setFechaNacimiento(dto.getFechaNacimiento());
        m.setSexo(dto.getSexo());
        m.setPropietario(propietario);
    }
}
