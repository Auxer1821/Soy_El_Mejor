package domain.sistemaPuntuacion;

import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import utils.PropertiesManager;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "control_puntos")
public class ControlDePuntos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha_apertura")
    private LocalDateTime fechaDeApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @OneToOne
    @JoinColumn(name = "humana_id", referencedColumnName = "id")
    private Humana colaboradorHumana;

    @OneToOne
    @JoinColumn(name = "juridica_id", referencedColumnName = "id")
    private Juridica colaboradorJuridica;

    @OneToMany
    @JoinColumn(name = "colaboracion_id", referencedColumnName = "id")
    private List<ColaboracionRealizada> colaboracionesRealizadas;

    @OneToMany
    @JoinColumn(name = "control_puntos_id", referencedColumnName = "id")
    private List<Canje> canjes;

    @Column(name = "puntos_al_cierre")
    private Double puntosAlCierre;

    @OneToOne
    @JoinColumn(name = "control_anterior_id", referencedColumnName = "id")
    private ControlDePuntos controlDePuntosAnterior;

    public void realizarCierre( ) {
        double puntosGanados = this.puntosGanados();

        double puntosActuales = this.puntosActuales();

        setFechaCierre(LocalDateTime.now());

        this.setPuntosAlCierre(puntosActuales + puntosGanados);

    }

    private Double puntosPerdidos() {

        return (double) this.canjes.stream()
                .mapToLong(canje -> canje.getBeneficioCanjeado().getPuntosNecesarios().longValue())
                .sum();
    }

    private Double puntosGanados() {
        return this.colaboracionesRealizadas.stream()
                .mapToDouble(ColaboracionRealizada::puntosGanados)
                .sum();
    }

    private Double puntosAnteriores() {
        double r = 0;
        if (controlDePuntosAnterior != null) r = this.controlDePuntosAnterior.getPuntosAlCierre();
        return r;//TODO hay que pasar la responsabilidad a los canje y desnormalizar los puntos canjeados
    }

    public Double puntosActuales() {
        return this.puntosAnteriores() - this.puntosPerdidos();
    }

}
