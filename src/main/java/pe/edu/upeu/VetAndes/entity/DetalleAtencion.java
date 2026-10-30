package pe.edu.upeu.VetAndes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/*
 * Linea de la atencion. precio_unitario es una COPIA del precio de la
 * prestacion en el momento de la atencion: si el catalogo cambia despues,
 * la atencion historica conserva su importe.
 */
@Entity
@Table(name = "detalle_atencion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atencion_id", nullable = false)
    private Atencion atencion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "prestacion_id", nullable = false)
    private Prestacion prestacion;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
}
