package pe.edu.upeu.VetAndes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.NumericBooleanConverter;

import java.math.BigDecimal;

/*
 * Servicio que ofrece la clinica (consulta, vacuna, bano...).
 * Tabla independiente: no depende de ninguna otra.
 */
@Entity
@Table(name = "prestacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Prestacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 7, unique = true)
    private String codigo;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    /* PERRO, GATO o TODAS */
    @Column(name = "especie_aplicable", nullable = false, length = 6)
    private String especieAplicable;

    /* La columna es NUMBER(1): el convertidor guarda true -> 1 y false -> 0 */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(nullable = false)
    private Boolean activo;

    /* RN-03: la prestacion sirve si es de la especie de la mascota o de TODAS */
    public boolean aplicaA(String especieMascota) {
        return "TODAS".equals(especieAplicable) || especieAplicable.equals(especieMascota);
    }
}
