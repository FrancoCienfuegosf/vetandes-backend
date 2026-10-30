package pe.edu.upeu.VetAndes.service;

import pe.edu.upeu.VetAndes.dto.AtencionesPorEspecieDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<AtencionesPorEspecieDTO> atencionesPorEspecie(LocalDate desde, LocalDate hasta);
}
