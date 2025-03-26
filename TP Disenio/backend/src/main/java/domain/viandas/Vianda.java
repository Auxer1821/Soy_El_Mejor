package domain.viandas;

import domain.heladera.Heladera;
import domain.ubicaciones.comunidades.Comunidad;
import domain.usuario.colaborador.Humana;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "viandas")
@NoArgsConstructor
@AllArgsConstructor
public class Vianda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="comida", nullable = false)
    private String comida;

    @ManyToOne
    @JoinColumn(name = "id_comunidad")
    private Comunidad comunidad;

    @ManyToOne
    @JoinColumn(name = "id_heladera")
    private Heladera heladera;

    @Column(name="calorias", nullable = false)
    private Integer calorias;

    @Column(name="peso", nullable = false)
    private Integer peso;

    @Column(name="fecha_caducidad", nullable = false)
    private LocalDate fechaDeCaducidad;

    @Column(name="fecha_colaboracion")
    private LocalDateTime fechaDeColaboracion;

    @ManyToOne
    @JoinColumn(name = "id_colaborador")
    private Humana colaborador;

    public Vianda(String comida, Heladera heladera, Integer calorias, Integer peso, LocalDate fechaDeCaducidad, LocalDateTime fechaDeColaboracion, Humana colaborador) {
        this.comida = comida;
        this.heladera = heladera;
        this.calorias = calorias;
        this.peso = peso;
        this.fechaDeCaducidad = fechaDeCaducidad;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.colaborador = colaborador;
    }

    public Vianda(String comida, Heladera heladera, Integer calorias, Integer peso, LocalDate fechaDeCaducidad, Humana colaborador) {
        this.comida = comida;
        this.heladera = heladera;
        this.calorias = calorias;
        this.peso = peso;
        this.fechaDeCaducidad = fechaDeCaducidad;
        this.colaborador = colaborador;
    }

    public Vianda(String comida, Comunidad comunidad, Integer calorias, Integer peso, LocalDate fechaDeCaducidad, LocalDateTime fechaDeColaboracion, Humana colaborador) {
        this.comida = comida;
        this.comunidad = comunidad;
        this.calorias = calorias;
        this.peso = peso;
        this.fechaDeCaducidad = fechaDeCaducidad;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.colaborador = colaborador;
    }

    //TODO
    public Boolean estaVencida(){
        return LocalDate.now().isAfter(fechaDeCaducidad);
    }

    public Boolean fueEntregada(){
        return true;
    }
}
