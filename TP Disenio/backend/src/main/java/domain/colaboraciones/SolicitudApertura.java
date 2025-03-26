package domain.colaboraciones;
import domain.heladera.Heladera;
import domain.tarjetas.Tarjeta;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "solicitudes_apertura")
@NoArgsConstructor
public class SolicitudApertura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_actual")
    private LocalDateTime fechaActual;

    @Column(name = "codigo_tarjeta", nullable = false)
    private String codigo_tarjeta;

    @ManyToOne
    @JoinColumn(name = "heladera_id", referencedColumnName = "id")
    private Heladera heladera;

    @OneToOne
    @JoinColumn(name = "donacion_v_id", referencedColumnName = "id")
    private DonacionVianda donacionVianda;

    @OneToOne
    @JoinColumn(name = "distribucion_v_id", referencedColumnName = "id")
    private DistribucionViandas distribucion;

    public SolicitudApertura(String tarjeta, LocalDateTime fechaActual, Heladera heladera) {
        this.codigo_tarjeta = tarjeta;
        this.fechaActual = fechaActual;
        this.heladera = heladera;
    }

    public SolicitudApertura(LocalDateTime fechaActual, String codigo_tarjeta, Heladera heladera, DonacionVianda donacionVianda) {
        this.fechaActual = fechaActual;
        this.codigo_tarjeta = codigo_tarjeta;
        this.heladera = heladera;
        this.donacionVianda = donacionVianda;
    }

    public SolicitudApertura(LocalDateTime fechaActual, String codigo_tarjeta, Heladera heladera, DistribucionViandas distribucion) {
        this.fechaActual = fechaActual;
        this.codigo_tarjeta = codigo_tarjeta;
        this.heladera = heladera;
        this.distribucion = distribucion;
    }
}
