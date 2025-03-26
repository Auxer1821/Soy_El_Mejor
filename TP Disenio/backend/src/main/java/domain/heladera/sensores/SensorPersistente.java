package domain.heladera.sensores;

import domain.heladera.Heladera;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sensores")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipoSensor", discriminatorType = DiscriminatorType.STRING)
public abstract class SensorPersistente {
    @Id
    private String id;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaDeColocacion;

    @ManyToOne
    @JoinColumn(name = "heladera_id", referencedColumnName = "id")
    private Heladera heladera;

    @Column(name = "tipoSensor", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TipoSensor tipoSensor;
}
