package domain.colaboraciones;

import domain.heladera.Heladera;
import domain.usuario.colaborador.Juridica;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Table(name = "encargos_heladera")
@NoArgsConstructor
public class EncargoHeladera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="fecha_de_contribucion")
    private LocalDateTime fechaDeContribucion;

    @OneToOne
    @JoinColumn(name = "heladera_id", nullable = false)
    private Heladera heladera;

    @OneToOne
    @JoinColumn(name = "colaborador_juridico_id", nullable = false)
    private Juridica colaboradorJuridico;

    public EncargoHeladera(LocalDateTime fechaDeContribucion, Heladera heladera, Juridica colaboradorJuridico) {
        this.fechaDeContribucion = fechaDeContribucion;
        this.heladera = heladera; //TODO: NO VA
        this.colaboradorJuridico = colaboradorJuridico;
    }
}
