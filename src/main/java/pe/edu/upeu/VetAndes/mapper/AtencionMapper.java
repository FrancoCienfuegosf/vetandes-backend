package pe.edu.upeu.VetAndes.mapper;

import org.springframework.stereotype.Component;
import pe.edu.upeu.VetAndes.dto.AtencionResponseDTO;
import pe.edu.upeu.VetAndes.dto.DetalleAtencionResponseDTO;
import pe.edu.upeu.VetAndes.entity.Atencion;
import pe.edu.upeu.VetAndes.entity.DetalleAtencion;
import pe.edu.upeu.VetAndes.entity.Mascota;

import java.util.List;

@Component
public class AtencionMapper {

    public AtencionResponseDTO toResponse(Atencion a) {
        Mascota m = a.getMascota();
        List<DetalleAtencionResponseDTO> detalles = a.getDetalles().stream()
                .map(this::toDetalle)
                .toList();
        return new AtencionResponseDTO(
                a.getId(), a.getNumero(), a.getFecha(),
                m.getId(), m.getNombre(), m.getEspecie(),
                m.getPropietario().getId(), m.getPropietario().getNombres(),
                a.getPeso(), a.getDiagnostico(), a.getEstado().name(),
                a.getSubtotal(), a.getDescuento(), a.getTotal(),
                a.getUsuarioRegistro(), a.getMotivoAnulacion(),
                detalles);
    }

    private DetalleAtencionResponseDTO toDetalle(DetalleAtencion d) {
        return new DetalleAtencionResponseDTO(
                d.getId(),
                d.getPrestacion().getId(), d.getPrestacion().getCodigo(), d.getPrestacion().getNombre(),
                d.getCantidad(), d.getPrecioUnitario(), d.getSubtotal());
    }
}
