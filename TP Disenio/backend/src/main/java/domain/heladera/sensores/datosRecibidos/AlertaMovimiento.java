package domain.heladera.sensores.datosRecibidos;

import domain.heladera.sensores.SensorMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "alertas_movimiento")
@AllArgsConstructor
@NoArgsConstructor
public class AlertaMovimiento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "sensorMovimiento", referencedColumnName = "id")
    private SensorMovimiento sensorMovimiento;

    @Column
    private LocalDateTime fechaDeAlerta;

    public AlertaMovimiento(String descripcion, SensorMovimiento sensor, LocalDateTime fechaDeAlerta) {
        this.descripcion = descripcion;
        this.sensorMovimiento = sensor;
        this.fechaDeAlerta = fechaDeAlerta;
    }
}
