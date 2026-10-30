package pe.edu.upeu.VetAndes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.dto.MascotaRequestDTO;
import pe.edu.upeu.VetAndes.dto.MascotaResponseDTO;
import pe.edu.upeu.VetAndes.entity.Mascota;
import pe.edu.upeu.VetAndes.entity.Propietario;
import pe.edu.upeu.VetAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.VetAndes.exception.ReglaNegocioException;
import pe.edu.upeu.VetAndes.mapper.MascotaMapper;
import pe.edu.upeu.VetAndes.repository.AtencionRepository;
import pe.edu.upeu.VetAndes.repository.MascotaRepository;
import pe.edu.upeu.VetAndes.repository.PropietarioRepository;
import pe.edu.upeu.VetAndes.service.MascotaService;

import java.util.List;

@Service
public class MascotaServiceImpl implements MascotaService {

    private static final Logger log = LoggerFactory.getLogger(MascotaServiceImpl.class);

    private final MascotaRepository mascotaRepository;
    private final PropietarioRepository propietarioRepository;
    private final AtencionRepository atencionRepository;
    private final MascotaMapper mascotaMapper;

    public MascotaServiceImpl(MascotaRepository mascotaRepository,
                              PropietarioRepository propietarioRepository,
                              AtencionRepository atencionRepository,
                              MascotaMapper mascotaMapper) {
        this.mascotaRepository = mascotaRepository;
        this.propietarioRepository = propietarioRepository;
        this.atencionRepository = atencionRepository;
        this.mascotaMapper = mascotaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MascotaResponseDTO> listar(Long propietarioId) {
        List<Mascota> mascotas;
        if (propietarioId == null) {
            mascotas = mascotaRepository.findAllByOrderByNombreAsc();
        } else {
            buscarPropietario(propietarioId); // 404 si no existe
            mascotas = mascotaRepository.findByPropietarioIdOrderByNombreAsc(propietarioId);
        }
        return mascotas.stream().map(mascotaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MascotaResponseDTO obtener(Long id) {
        return mascotaMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public MascotaResponseDTO registrar(MascotaRequestDTO request) {
        // RN-02: el propietario debe existir (404)
        Propietario propietario = buscarPropietario(request.getPropietarioId());
        String nombre = request.getNombre().trim();
        // RN-02: sin dos mascotas con el mismo nombre para el mismo propietario (409)
        if (mascotaRepository.existsByNombreIgnoreCaseAndPropietarioId(nombre, propietario.getId())) {
            throw new ReglaNegocioException("El propietario " + propietario.getNombres()
                    + " ya tiene una mascota llamada " + nombre);
        }
        Mascota mascota = new Mascota();
        mascotaMapper.copiar(request, mascota, propietario);
        Mascota guardada = mascotaRepository.save(mascota);
        log.info("Mascota registrada | id={} | nombre={} | propietarioId={}",
                guardada.getId(), guardada.getNombre(), propietario.getId());
        return mascotaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public MascotaResponseDTO actualizar(Long id, MascotaRequestDTO request) {
        Mascota mascota = buscar(id);
        Propietario propietario = buscarPropietario(request.getPropietarioId());
        String nombre = request.getNombre().trim();
        if (mascotaRepository.existsByNombreIgnoreCaseAndPropietarioIdAndIdNot(
                nombre, propietario.getId(), id)) {
            throw new ReglaNegocioException("El propietario " + propietario.getNombres()
                    + " ya tiene una mascota llamada " + nombre);
        }
        mascotaMapper.copiar(request, mascota, propietario);
        Mascota guardada = mascotaRepository.saveAndFlush(mascota);
        log.info("Mascota actualizada | id={} | nombre={}", id, guardada.getNombre());
        return mascotaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Mascota mascota = buscar(id);
        // RN-02: una mascota con atenciones no se elimina (409)
        if (atencionRepository.existsByMascotaId(id)) {
            throw new ReglaNegocioException("La mascota " + mascota.getNombre()
                    + " tiene atenciones registradas y no se puede eliminar");
        }
        mascotaRepository.delete(mascota);
        log.info("Mascota eliminada | id={} | nombre={}", id, mascota.getNombre());
    }

    private Mascota buscar(Long id) {
        return mascotaRepository.findWithPropietarioById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Mascota no encontrada con id: " + id));
    }

    private Propietario buscarPropietario(Long id) {
        return propietarioRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Propietario no encontrado con id: " + id));
    }
}
