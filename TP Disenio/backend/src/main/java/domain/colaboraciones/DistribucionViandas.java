package domain.colaboraciones;

import domain.heladera.Heladera;
import domain.usuario.colaborador.Humana;
import lombok.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "distribuciones_viandas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class DistribucionViandas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_viandas", nullable = false)
    private Integer cantidadViandas;

    @ManyToOne
    @JoinColumn(name = "id_heladera_dest")
    private Heladera heladeraDestino;

    @ManyToOne
    @JoinColumn(name = "id_heladera_origen")
    private Heladera heladeraOrigen;

    @Enumerated(EnumType.STRING)
    private MotivoDistribucion motivo;

    @Enumerated(EnumType.STRING)
    private EstadoDonacion estado;

    @ManyToOne
    @JoinColumn(name = "id_humana")
    private Humana humana;

    @Column(name ="fecha_de_colaboracion")
    private LocalDateTime fechaDeColaboracion;

    @Column(name ="fecha_de_realizacion")
    private LocalDateTime fechaDeRealizacion;

    public DistribucionViandas(Integer cantidadViandas, Heladera heladeraDestino, Heladera heladeraOrigen, MotivoDistribucion motivo, Humana humana) throws MqttException, InterruptedException {
        this.cantidadViandas = cantidadViandas;
        this.heladeraDestino = heladeraDestino;
        this.heladeraOrigen = heladeraOrigen;
        this.motivo = motivo;
        this.humana = humana;
        this.fechaDeColaboracion = LocalDateTime.now();
    }

    public DistribucionViandas(Integer cantidadViandas, Heladera heladeraDestino, Heladera heladeraOrigen, MotivoDistribucion motivo) throws MqttException, InterruptedException {
        this.cantidadViandas = cantidadViandas;
        this.heladeraDestino = heladeraDestino;
        this.heladeraOrigen = heladeraOrigen;
        this.motivo = motivo;
        this.fechaDeColaboracion = LocalDateTime.now();
    }

    public DistribucionViandas(Integer cantidadViandas, LocalDateTime fechaDeColaboracion, Humana humana) throws MqttException, InterruptedException {
        this.cantidadViandas = cantidadViandas;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.humana = humana;
    }
}
