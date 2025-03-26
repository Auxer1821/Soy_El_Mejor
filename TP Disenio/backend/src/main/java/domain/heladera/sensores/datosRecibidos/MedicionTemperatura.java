package domain.heladera.sensores.datosRecibidos;

import domain.heladera.sensores.SensorTemperatura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "mediciones_temperatura")
@NoArgsConstructor
@AllArgsConstructor
public class MedicionTemperatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sensorTemperatura", referencedColumnName = "id")
    private SensorTemperatura sensorTemperatura;

    @Column
    private LocalDateTime fechaMedicion;

    @Column
    private Double temperaturaRegistrada;

    public MedicionTemperatura(LocalDateTime fechaMedicion, SensorTemperatura sensorTemperatura, Double temperaturaRegistrada) {
        this.fechaMedicion = fechaMedicion;
        this.sensorTemperatura = sensorTemperatura;
        this.temperaturaRegistrada = temperaturaRegistrada;
    }
}

