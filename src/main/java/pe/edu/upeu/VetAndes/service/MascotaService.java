package pe.edu.upeu.VetAndes.service;

import pe.edu.upeu.VetAndes.dto.MascotaRequestDTO;
import pe.edu.upeu.VetAndes.dto.MascotaResponseDTO;

import java.util.List;

public interface MascotaService {
    List<MascotaResponseDTO> listar(Long propietarioId);
    MascotaResponseDTO obtener(Long id);
    MascotaResponseDTO registrar(MascotaRequestDTO request);
    MascotaResponseDTO actualizar(Long id, MascotaRequestDTO request);
    void eliminar(Long id);
}
