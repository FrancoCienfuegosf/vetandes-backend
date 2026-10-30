package pe.edu.upeu.VetAndes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/*
 * Tabla dependiente: cada mascota pertenece a un propietario.
 *
 * @ManyToOne con carga diferida (LAZY): al leer una mascota no se trae al
 * propietario hasta que se usa. Las consultas del repositorio usan
 * @EntityGraph para traerlo en el mismo SELECT y evitar el problema N+1.
 */
@Entity
@Table(name = "mascota")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String nombre;

    /* PERRO, GATO u OTRO */
    @Column(nullable = false, length = 6)
    private String especie;

    @Column(length = 40)
    private String raza;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /* M (macho) o H (hembra) */
    @Column(nullable = false, length = 1)
    private String sexo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;
}
