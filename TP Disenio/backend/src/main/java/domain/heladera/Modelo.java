package domain.heladera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "modelos_heladeras")
public class Modelo  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre_modelo")
    private String nombre;

    @Column(name = "minTemperatura", nullable = false)
    private Double temperaturaMinima;

    @Column(name = "maxTemperatura", nullable = false)
    private Double temperaturaMaxima;

    @Column(name = "maxCantidadViandas", nullable = false)
    private Integer maxCantidadViandas;

    public Modelo(String nombre, Double temperaturaMinima, Double temperaturaMaxima, Integer maxCantidadViandas) {
        this.nombre = nombre;
        this.temperaturaMinima = temperaturaMinima;
        this.temperaturaMaxima = temperaturaMaxima;
        this.maxCantidadViandas = maxCantidadViandas;
    }
}
