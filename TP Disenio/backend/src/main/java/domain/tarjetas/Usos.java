package domain.tarjetas;

import domain.heladera.Heladera;
import lombok.*;
import net.bytebuddy.asm.Advice;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "usos")
@NoArgsConstructor
@AllArgsConstructor
public class Usos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "codigo_tarjeta", referencedColumnName = "codigo")
    private Tarjeta tarjeta_usada;

    @Column(name = "fecha_uso")
    private LocalDate fechaDeUso;

    @ManyToOne
    @JoinColumn(name = "heladera_id")
    private Heladera heladera;

    public Usos (LocalDate fechaDeUso, Heladera heladera, Tarjeta tarjeta){
        this.fechaDeUso = fechaDeUso;
        this.heladera = heladera;
        this.tarjeta_usada = tarjeta;
    }

    public boolean fueUsadaHoy() {
        return Objects.equals(fechaDeUso, LocalDate.now());
    }
}
