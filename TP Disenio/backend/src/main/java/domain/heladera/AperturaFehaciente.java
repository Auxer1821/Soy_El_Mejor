package domain.heladera;

import domain.usuario.colaborador.Humana;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "aperturas_fehacientes")
@NoArgsConstructor
public class AperturaFehaciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "humana_id")
    private Humana humana;

    @ManyToOne
    @JoinColumn(name = "heladera_id", referencedColumnName = "id")
    private Heladera heladera;

    @Column(name = "fecha_de_apertura", nullable = false)
    private LocalDateTime fechaDeApertura;

    public AperturaFehaciente(Humana humana, Heladera heladera, LocalDateTime fechaDeApertura) {
        this.humana = humana;
        this.heladera = heladera;
        this.fechaDeApertura = fechaDeApertura;
    }
}
