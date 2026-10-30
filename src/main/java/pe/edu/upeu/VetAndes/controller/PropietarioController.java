package pe.edu.upeu.VetAndes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.VetAndes.dto.PropietarioResponseDTO;
import pe.edu.upeu.VetAndes.service.PropietarioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @GetMapping
    public ResponseEntity<List<PropietarioResponseDTO>> listar() {
        return ResponseEntity.ok(propietarioService.listar());
    }
}
