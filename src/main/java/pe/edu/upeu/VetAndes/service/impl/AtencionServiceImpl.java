package pe.edu.upeu.VetAndes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.dto.AtencionRequestDTO;
import pe.edu.upeu.VetAndes.dto.AtencionResponseDTO;
import pe.edu.upeu.VetAndes.dto.DetalleAtencionRequestDTO;
import pe.edu.upeu.VetAndes.entity.*;
import pe.edu.upeu.VetAndes.exception.RecursoNoEncontradoException;
import pe.edu.upeu.VetAndes.exception.ReglaNegocioException;
import pe.edu.upeu.VetAndes.exception.SolicitudInvalidaException;
import pe.edu.upeu.VetAndes.mapper.AtencionMapper;
import pe.edu.upeu.VetAndes.repository.AtencionRepository;
import pe.edu.upeu.VetAndes.repository.MascotaRepository;
import pe.edu.upeu.VetAndes.repository.PrestacionRepository;
import pe.edu.upeu.VetAndes.service.AtencionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AtencionServiceImpl implements AtencionService {

    private static final Logger log = LoggerFactory.getLogger(AtencionServiceImpl.class);

    // RN-04
    private static final int PRESTACIONES_PARA_DESCUENTO = 3;
    private static final BigDecimal PORCENTAJE_DESCUENTO = new BigDecimal("0.10");
    // RN-05
    private static final long HORAS_MAXIMAS_ANULACION = 48;

    private final AtencionRepository atencionRepository;
    private final MascotaRepository mascotaRepository;
    private final PrestacionRepository prestacionRepository;
    private final AtencionMapper atencionMapper;

    public AtencionServiceImpl(AtencionRepository atencionRepository,
                               MascotaRepository mascotaRepository,
                               PrestacionRepository prestacionRepository,
                               AtencionMapper atencionMapper) {
        this.atencionRepository = atencionRepository;
        this.mascotaRepository = mascotaRepository;
        this.prestacionRepository = prestacionRepository;
        this.atencionMapper = atencionMapper;
    }

    /*
     * Registro cabecera-detalle ATOMICO.
     *
     * @Transactional abre UNA transaccion para todo el metodo: la cabecera
     * y todos sus detalles se confirman juntos (commit) al terminar. Si
     * cualquier detalle lanza una excepcion (RuntimeException), Spring hace
     * rollback y no queda nada guardado a medias.
     *
     * Los importes NO vienen del cliente: el precio unitario se lee de la
     * base de datos y subtotal, descuento y total se calculan aqui.
     */
    @Override
    @Transactional
    public AtencionResponseDTO registrar(AtencionRequestDTO request, String usuarioRegistro) {
        Mascota mascota = mascotaRepository.findWithPropietarioById(request.getMascotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Mascota no encontrada con id: " + request.getMascotaId()));

        // RN-03: la misma prestacion no se repite (400)
        Set<Long> vistas = new HashSet<>();
        for (int i = 0; i < request.getDetalles().size(); i++) {
            Long prestacionId = request.getDetalles().get(i).getPrestacionId();
            if (!vistas.add(prestacionId)) {
                throw new SolicitudInvalidaException("Detalle " + (i + 1)
                        + ": la prestación con id " + prestacionId + " está repetida");
            }
        }

        Atencion atencion = new Atencion();
        atencion.setNumero(siguienteNumero());
        atencion.setFecha(LocalDateTime.now());
        atencion.setMascota(mascota);
        atencion.setPeso(request.getPeso());
        atencion.setDiagnostico(request.getDiagnostico() == null ? null : request.getDiagnostico().trim());
        atencion.setEstado(EstadoAtencion.REGISTRADA);
        atencion.setUsuarioRegistro(usuarioRegistro);

        BigDecimal subtotal = BigDecimal.ZERO;
        int linea = 0;
        for (DetalleAtencionRequestDTO item : request.getDetalles()) {
            linea++;
            Prestacion prestacion = prestacionRepository.findById(item.getPrestacionId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Prestación no encontrada con id: " + item.getPrestacionId()));

            // RN-03: la prestacion debe estar activa (409)
            if (!Boolean.TRUE.equals(prestacion.getActivo())) {
                throw new ReglaNegocioException("Detalle " + linea + ": la prestación "
                        + prestacion.getCodigo() + " - " + prestacion.getNombre() + " está inactiva");
            }
            // RN-03: debe aplicar a la especie de la mascota o a TODAS (409)
            if (!prestacion.aplicaA(mascota.getEspecie())) {
                throw new ReglaNegocioException("Detalle " + linea + ": la prestación "
                        + prestacion.getCodigo() + " - " + prestacion.getNombre()
                        + " es solo para " + prestacion.getEspecieAplicable()
                        + " y la mascota " + mascota.getNombre() + " es " + mascota.getEspecie());
            }

            BigDecimal subtotalLinea = prestacion.getPrecio()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));

            DetalleAtencion detalle = new DetalleAtencion();
            detalle.setPrestacion(prestacion);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(prestacion.getPrecio()); // precio de la BD
            detalle.setSubtotal(subtotalLinea);
            atencion.agregarDetalle(detalle);

            subtotal = subtotal.add(subtotalLinea);
        }

        // RN-04: 3 o mas prestaciones distintas -> 10 % de descuento
        BigDecimal descuento = atencion.getDetalles().size() >= PRESTACIONES_PARA_DESCUENTO
                ? subtotal.multiply(PORCENTAJE_DESCUENTO).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2);
        atencion.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        atencion.setDescuento(descuento);
        atencion.setTotal(atencion.getSubtotal().subtract(descuento));

        // cascade = ALL: un solo save inserta la cabecera y sus detalles
        Atencion guardada = atencionRepository.save(atencion);
        log.info("Atención registrada | numero={} | mascota={} | detalles={} | subtotal={} | descuento={} | total={} | usuario={}",
                guardada.getNumero(), mascota.getNombre(), guardada.getDetalles().size(),
                guardada.getSubtotal(), guardada.getDescuento(), guardada.getTotal(), usuarioRegistro);
        return atencionMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtencionResponseDTO> buscar(String mascota, String especie, EstadoAtencion estado,
                                            LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("El rango de fechas es inválido: 'desde' ("
                    + desde + ") es posterior a 'hasta' (" + hasta + ")");
        }
        String patronMascota = (mascota == null || mascota.isBlank())
                ? null
                : "%" + mascota.trim().toLowerCase() + "%";
        String filtroEspecie = (especie == null || especie.isBlank())
                ? null
                : especie.trim().toUpperCase();
        return atencionRepository.buscar(patronMascota, filtroEspecie, estado,
                        inicioDelDia(desde), finDelDia(hasta))
                .stream()
                .map(atencionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AtencionResponseDTO obtener(Long id) {
        return atencionMapper.toResponse(buscarCompleta(id));
    }

    /* RN-05: solo REGISTRADA y dentro de las 48 horas desde su registro */
    @Override
    @Transactional
    public AtencionResponseDTO anular(Long id, String motivo) {
        Atencion atencion = buscarCompleta(id);
        if (atencion.getEstado() != EstadoAtencion.REGISTRADA) {
            throw new ReglaNegocioException("La atención " + atencion.getNumero()
                    + " ya está " + atencion.getEstado() + " y no se puede anular");
        }
        LocalDateTime limite = atencion.getFecha().plusHours(HORAS_MAXIMAS_ANULACION);
        if (LocalDateTime.now().isAfter(limite)) {
            throw new ReglaNegocioException("La atención " + atencion.getNumero()
                    + " tiene más de " + HORAS_MAXIMAS_ANULACION + " horas desde su registro y no se puede anular");
        }
        atencion.setEstado(EstadoAtencion.ANULADA);
        atencion.setMotivoAnulacion(motivo.trim());
        log.info("Atención anulada | numero={} | motivo={}", atencion.getNumero(), atencion.getMotivoAnulacion());
        return atencionMapper.toResponse(atencion);
    }

    /* AT-000001 correlativo: toma el ultimo numero y le suma 1 */
    private String siguienteNumero() {
        String ultimo = atencionRepository.ultimoNumero();
        int siguiente = (ultimo == null) ? 1 : Integer.parseInt(ultimo.substring(3)) + 1;
        return String.format("AT-%06d", siguiente);
    }

    private Atencion buscarCompleta(Long id) {
        return atencionRepository.buscarCompleta(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Atención no encontrada con id: " + id));
    }

    private LocalDateTime inicioDelDia(LocalDate fecha) {
        return fecha == null ? null : fecha.atStartOfDay();
    }

    private LocalDateTime finDelDia(LocalDate fecha) {
        return fecha == null ? null : fecha.atTime(LocalTime.MAX);
    }
}
