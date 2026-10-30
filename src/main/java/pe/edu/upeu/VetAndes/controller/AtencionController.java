package pe.edu.upeu.VetAndes.controller;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.VetAndes.dto.AnularAtencionRequestDTO;
import pe.edu.upeu.VetAndes.dto.AtencionRequestDTO;
import pe.edu.upeu.VetAndes.dto.AtencionResponseDTO;
import pe.edu.upeu.VetAndes.entity.EstadoAtencion;
import pe.edu.upeu.VetAndes.service.AtencionService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/atenciones")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    /* 201 con la atencion completa; el usuario de registro sale del token */
    @PostMapping
    public ResponseEntity<AtencionResponseDTO> registrar(@Valid @RequestBody AtencionRequestDTO request,
                                                         Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(atencionService.registrar(request, authentication.getName()));
    }

    /* Filtros opcionales y combinables; sin resultados -> 200 con [] */
    @GetMapping
    public ResponseEntity<List<AtencionResponseDTO>> buscar(
            @RequestParam(required = false) String mascota,
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) EstadoAtencion estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(atencionService.buscar(mascota, especie, estado, desde, hasta));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtencionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(atencionService.obtener(id));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<AtencionResponseDTO> anular(@PathVariable Long id,
                                                      @Valid @RequestBody AnularAtencionRequestDTO request) {
        return ResponseEntity.ok(atencionService.anular(id, request.getMotivo()));
    }
}
