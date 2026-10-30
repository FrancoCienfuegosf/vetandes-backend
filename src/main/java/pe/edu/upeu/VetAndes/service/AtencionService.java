package pe.edu.upeu.VetAndes.service;

import pe.edu.upeu.VetAndes.dto.AtencionRequestDTO;
import pe.edu.upeu.VetAndes.dto.AtencionResponseDTO;
import pe.edu.upeu.VetAndes.entity.EstadoAtencion;

import java.time.LocalDate;
import java.util.List;

public interface AtencionService {
    AtencionResponseDTO registrar(AtencionRequestDTO request, String usuarioRegistro);
    List<AtencionResponseDTO> buscar(String mascota, String especie, EstadoAtencion estado,
                                     LocalDate desde, LocalDate hasta);
    AtencionResponseDTO obtener(Long id);
    AtencionResponseDTO anular(Long id, String motivo);
}
