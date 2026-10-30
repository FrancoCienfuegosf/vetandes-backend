package pe.edu.upeu.VetAndes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.dto.PrestacionRequestDTO;
import pe.edu.upeu.VetAndes.dto.PrestacionResponseDTO;
import pe.edu.upeu.VetAndes.entity.Prestacion;
import pe.edu.upeu.VetAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.VetAndes.exception.ReglaNegocioException;
import pe.edu.upeu.VetAndes.exception.SolicitudInvalidaException;
import pe.edu.upeu.VetAndes.mapper.PrestacionMapper;
import pe.edu.upeu.VetAndes.repository.DetalleAtencionRepository;
import pe.edu.upeu.VetAndes.repository.PrestacionRepository;
import pe.edu.upeu.VetAndes.service.PrestacionService;

import java.util.List;
import java.util.Set;

@Service
public class PrestacionServiceImpl implements PrestacionService {

    private static final Logger log = LoggerFactory.getLogger(PrestacionServiceImpl.class);
    private static final Set<String> ESPECIES = Set.of("PERRO", "GATO", "OTRO", "TODAS");

    private final PrestacionRepository prestacionRepository;
    private final DetalleAtencionRepository detalleAtencionRepository;
    private final PrestacionMapper prestacionMapper;

    public PrestacionServiceImpl(PrestacionRepository prestacionRepository,
                                 DetalleAtencionRepository detalleAtencionRepository,
                                 PrestacionMapper prestacionMapper) {
        this.prestacionRepository = prestacionRepository;
        this.detalleAtencionRepository = detalleAtencionRepository;
        this.prestacionMapper = prestacionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestacionResponseDTO> listar(String especie, Boolean activo) {
        String filtro = (especie == null || especie.isBlank()) ? null : especie.trim().toUpperCase();
        if (filtro != null && !ESPECIES.contains(filtro)) {
            throw new SolicitudInvalidaException("La especie debe ser PERRO, GATO, OTRO o TODAS");
        }
        return prestacionRepository.buscar(filtro, activo).stream()
                .map(prestacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PrestacionResponseDTO obtener(Long id) {
        return prestacionMapper.toResponse(buscar(id));
    }

    @Override
    @Transactional
    public PrestacionResponseDTO registrar(PrestacionRequestDTO request) {
        String codigo = request.getCodigo().trim().toUpperCase();
        // RN-01: codigo unico
        if (prestacionRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ReglaNegocioException("Ya existe una prestación con el código " + codigo);
        }
        Prestacion prestacion = new Prestacion();
        prestacion.setActivo(true);
        prestacionMapper.copiar(request, prestacion);
        Prestacion guardada = prestacionRepository.save(prestacion);
        log.info("Prestación registrada | id={} | codigo={}", guardada.getId(), guardada.getCodigo());
        return prestacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public PrestacionResponseDTO actualizar(Long id, PrestacionRequestDTO request) {
        Prestacion prestacion = buscar(id);
        String codigo = request.getCodigo().trim().toUpperCase();
        // RN-01: el nuevo codigo no puede pertenecer a OTRA prestacion
        if (prestacionRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new ReglaNegocioException("Ya existe una prestación con el código " + codigo);
        }
        prestacionMapper.copiar(request, prestacion);
        Prestacion guardada = prestacionRepository.saveAndFlush(prestacion);
        log.info("Prestación actualizada | id={} | codigo={}", id, guardada.getCodigo());
        return prestacionMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Prestacion prestacion = buscar(id);
        // RN-01: si ya se uso en una atencion, borrarla romperia el historial
        if (detalleAtencionRepository.existsByPrestacionId(id)) {
            throw new ReglaNegocioException("La prestación " + prestacion.getCodigo()
                    + " está usada en atenciones y no se puede eliminar. "
                    + "Use PATCH /api/v1/prestaciones/" + id + "/estado para darla de baja");
        }
        prestacionRepository.delete(prestacion);
        log.info("Prestación eliminada | id={} | codigo={}", id, prestacion.getCodigo());
    }

    @Override
    @Transactional
    public PrestacionResponseDTO cambiarEstado(Long id, boolean activo) {
        Prestacion prestacion = buscar(id);
        prestacion.setActivo(activo);
        log.info("Prestación {} | id={} | codigo={}",
                activo ? "reactivada" : "dada de baja", id, prestacion.getCodigo());
        return prestacionMapper.toResponse(prestacionRepository.save(prestacion));
    }

    private Prestacion buscar(Long id) {
        return prestacionRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Prestación no encontrada con id: " + id));
    }
}
