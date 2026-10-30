package pe.edu.upeu.VetAndes.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.dto.PropietarioResponseDTO;
import pe.edu.upeu.VetAndes.mapper.PropietarioMapper;
import pe.edu.upeu.VetAndes.repository.PropietarioRepository;
import pe.edu.upeu.VetAndes.service.PropietarioService;

import java.util.List;

@Service
public class PropietarioServiceImpl implements PropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final PropietarioMapper propietarioMapper;

    public PropietarioServiceImpl(PropietarioRepository propietarioRepository,
                                  PropietarioMapper propietarioMapper) {
        this.propietarioRepository = propietarioRepository;
        this.propietarioMapper = propietarioMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropietarioResponseDTO> listar() {
        return propietarioRepository.findAllByOrderByNombresAsc().stream()
                .map(propietarioMapper::toResponse)
                .toList();
    }
}
