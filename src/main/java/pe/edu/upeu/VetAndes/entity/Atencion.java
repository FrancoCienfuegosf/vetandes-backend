package pe.edu.upeu.VetAndes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 * Cabecera de la atencion.
 *
 * detalles: cascade ALL + orphanRemoval -> al guardar la cabecera se
 * guardan tambien sus detalles, en la misma transaccion.
 */
@Entity
@Table(name = "atencion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* AT-000001, AT-000002... */
    @Column(nullable = false, length = 9, unique = true, updatable = false)
    private String numero;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mascota_id", nullable = false)
    private Mascota mascota;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @Column(length = 300)
    private String diagnostico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 12)
    private EstadoAtencion estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuento;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "usuario_registro", nullable = false, length = 30, updatable = false)
    private String usuarioRegistro;

    @Column(name = "motivo_anulacion", length = 200)
    private String motivoAnulacion;

    @OneToMany(mappedBy = "atencion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleAtencion> detalles = new ArrayList<>();

    /* Mantiene los dos lados de la relacion sincronizados */
    public void agregarDetalle(DetalleAtencion detalle) {
        detalles.add(detalle);
        detalle.setAtencion(this);
    }
}
