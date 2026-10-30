package pe.edu.upeu.VetAndes.service;

import pe.edu.upeu.VetAndes.dto.PrestacionRequestDTO;
import pe.edu.upeu.VetAndes.dto.PrestacionResponseDTO;

import java.util.List;

public interface PrestacionService {
    List<PrestacionResponseDTO> listar(String especie, Boolean activo);
    PrestacionResponseDTO obtener(Long id);
    PrestacionResponseDTO registrar(PrestacionRequestDTO request);
    PrestacionResponseDTO actualizar(Long id, PrestacionRequestDTO request);
    void eliminar(Long id);
    PrestacionResponseDTO cambiarEstado(Long id, boolean activo);
}
