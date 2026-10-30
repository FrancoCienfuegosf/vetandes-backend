package pe.edu.upeu.VetAndes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.VetAndes.dto.MascotaRequestDTO;
import pe.edu.upeu.VetAndes.dto.MascotaResponseDTO;
import pe.edu.upeu.VetAndes.service.MascotaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /* ?propietarioId=1 filtra; 404 si el propietario no existe */
    @GetMapping
    public ResponseEntity<List<MascotaResponseDTO>> listar(@RequestParam(required = false) Long propietarioId) {
        return ResponseEntity.ok(mascotaService.listar(propietarioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<MascotaResponseDTO> registrar(@Valid @RequestBody MascotaRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaResponseDTO> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody MascotaRequestDTO request) {
        return ResponseEntity.ok(mascotaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mascotaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
