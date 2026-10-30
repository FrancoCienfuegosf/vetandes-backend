package pe.edu.upeu.VetAndes.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.VetAndes.dto.AtencionesPorEspecieDTO;
import pe.edu.upeu.VetAndes.exception.SolicitudInvalidaException;
import pe.edu.upeu.VetAndes.repository.AtencionRepository;
import pe.edu.upeu.VetAndes.service.ReporteService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteServiceImpl.class);

    private final AtencionRepository atencionRepository;

    public ReporteServiceImpl(AtencionRepository atencionRepository) {
        this.atencionRepository = atencionRepository;
    }

    /* La agregacion la hace Oracle con la consulta JPQL (GROUP BY) */
    @Override
    @Transactional(readOnly = true)
    public List<AtencionesPorEspecieDTO> atencionesPorEspecie(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new SolicitudInvalidaException("El rango de fechas es inválido: 'desde' ("
                    + desde + ") es posterior a 'hasta' (" + hasta + ")");
        }
        List<AtencionesPorEspecieDTO> filas = atencionRepository.reportePorEspecie(
                desde == null ? null : desde.atStartOfDay(),
                hasta == null ? null : hasta.atTime(LocalTime.MAX));
        log.info("Reporte atenciones por especie | desde={} | hasta={} | filas={}", desde, hasta, filas.size());
        return filas;
    }
}
