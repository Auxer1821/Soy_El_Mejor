package domain.colaboraciones;

import domain.usuario.colaborador.Humana;
import domain.viandas.Vianda;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "donaciones_viandas")
@NoArgsConstructor
@AllArgsConstructor
public class DonacionVianda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_humana", nullable = false)
    private Humana humana;

    @Column(name = "fecha_de_colaboracion")
    private LocalDateTime fechaDeColaboracion;

    @OneToOne
    @JoinColumn(name = "vianda_id")
    private Vianda vianda;

    @Enumerated(EnumType.STRING)
    private EstadoDonacion estadoDonacion;

    @Column(name = "fecha_de_realizacion")
    private LocalDateTime fechaDeRealizacion;

    public DonacionVianda(Humana humana, LocalDateTime fechaDeColaboracion, Vianda vianda, EstadoDonacion estadoDonacion) {
        this.humana = humana;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.vianda = vianda;
        this.estadoDonacion = estadoDonacion;
    }

    public DonacionVianda(Humana humana, LocalDateTime fechaDeColaboracion, Vianda vianda){
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.vianda = vianda;
        this.humana = humana;
    }

    public DonacionVianda(Humana humana, LocalDateTime fechaDeColaboracion) {
        this.humana = humana;
        this.fechaDeColaboracion = fechaDeColaboracion;
    }
}
