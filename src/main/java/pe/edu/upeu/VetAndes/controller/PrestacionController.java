package pe.edu.upeu.VetAndes.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.upeu.VetAndes.dto.PrestacionEstadoRequestDTO;
import pe.edu.upeu.VetAndes.dto.PrestacionRequestDTO;
import pe.edu.upeu.VetAndes.dto.PrestacionResponseDTO;
import pe.edu.upeu.VetAndes.service.PrestacionService;

import java.util.List;

/*
 * El controlador solo recibe, valida (@Valid) y delega en el servicio.
 * No usa repositorios ni revisa roles: los permisos estan en SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/prestaciones")
public class PrestacionController {

    private final PrestacionService prestacionService;

    public PrestacionController(PrestacionService prestacionService) {
        this.prestacionService = prestacionService;
    }

    /* ?especie=PERRO -> las de PERRO y las de TODAS; ?activo=true -> solo activas */
    @GetMapping
    public ResponseEntity<List<PrestacionResponseDTO>> listar(
            @RequestParam(required = false) String especie,
            @RequestParam(required = false) Boolean activo) {
        return ResponseEntity.ok(prestacionService.listar(especie, activo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrestacionResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(prestacionService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<PrestacionResponseDTO> registrar(@Valid @RequestBody PrestacionRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestacionService.registrar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrestacionResponseDTO> actualizar(@PathVariable Long id,
                                                            @Valid @RequestBody PrestacionRequestDTO request) {
        return ResponseEntity.ok(prestacionService.actualizar(id, request));
    }

    /* 204 si se elimino; 409 si ya se uso en atenciones (RN-01) */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /* Baja logica: { "activo": false }. Tambien permite reactivar */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PrestacionResponseDTO> cambiarEstado(@PathVariable Long id,
                                                               @Valid @RequestBody PrestacionEstadoRequestDTO request) {
        return ResponseEntity.ok(prestacionService.cambiarEstado(id, request.getActivo()));
    }
}
