package domain.colaboraciones;

import domain.forms.FormularioCompletado;
import domain.tarjetas.Tarjeta;
import domain.usuario.colaborador.Humana;
import domain.usuario.vulnerable.PersonaVulnerable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "registros_personas_vulnerables")
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDePersonasVulnerables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "vulnerable_id")
    private PersonaVulnerable vulnerable;

    @OneToOne
    @JoinColumn(name = "codigo")
    private Tarjeta tarjetaAsociada;

    @Transient
    private FormularioCompletado formularioCompletado;

    @OneToOne
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Humana colaboradorHumano;

    @Column(name = "fecha_de_colaboracion")
    private LocalDate fechaDeColaboracion;

    public RegistroDePersonasVulnerables (PersonaVulnerable vulnerable, Humana colaboradorHumano) {
        this.fechaDeColaboracion = LocalDate.now();
        this.vulnerable = vulnerable;
        this.colaboradorHumano = colaboradorHumano;
    }

    public RegistroDePersonasVulnerables(PersonaVulnerable vulnerable, Tarjeta tarjetaAsociada, Humana colaboradorHumano, LocalDate fechaDeColaboracion) {
        this.vulnerable = vulnerable;
        this.tarjetaAsociada = tarjetaAsociada;
        this.colaboradorHumano = colaboradorHumano;
        this.fechaDeColaboracion = fechaDeColaboracion;
    }

    public RegistroDePersonasVulnerables(Humana colaboradorHumano, LocalDate fechaDeColaboracion) {
        this.colaboradorHumano = colaboradorHumano;
        this.fechaDeColaboracion = fechaDeColaboracion;
    }
}
