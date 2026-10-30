package pe.edu.upeu.VetAndes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.dto.PrestacionRequestDTO;
import pe.edu.upeu.VetAndes.dto.PrestacionResponseDTO;
import pe.edu.upeu.VetAndes.entity.Prestacion;

@Component
public class PrestacionMapper {

    public PrestacionResponseDTO toResponse(Prestacion p) {
        return new PrestacionResponseDTO(
                p.getId(), p.getCodigo(), p.getNombre(), p.getPrecio(),
                p.getEspecieAplicable(), p.getActivo());
    }

    /* Copia los datos del request a la entidad (alta y edicion) */
    public void copiar(PrestacionRequestDTO dto, Prestacion p) {
        p.setCodigo(dto.getCodigo().trim().toUpperCase());
        p.setNombre(dto.getNombre().trim());
        p.setPrecio(dto.getPrecio());
        p.setEspecieAplicable(dto.getEspecieAplicable());
        if (dto.getActivo() != null) {
            p.setActivo(dto.getActivo());
        }
    }
}
