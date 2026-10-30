package pe.edu.upeu.VetAndes.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.VetAndes.dto.AtencionesPorEspecieDTO;
import pe.edu.upeu.VetAndes.service.ReporteService;

import java.time.LocalDate;
import java.util.List;

/*
 * @PreAuthorize exige el PERMISO REPORTE_VER (no el rol). Es una segunda
 * barrera ademas de la regla de SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/reportes")
@PreAuthorize("hasAuthority('REPORTE_VER')")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/atenciones-por-especie")
    public ResponseEntity<List<AtencionesPorEspecieDTO>> atencionesPorEspecie(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(reporteService.atencionesPorEspecie(desde, hasta));
    }
}
