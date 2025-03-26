package domain.suscripciones;

import domain.comunicaciones.Contacto;
import domain.comunicaciones.Mensaje;
import domain.comunicaciones.Notificador;
import domain.usuario.colaborador.Humana;
import domain.heladera.Heladera;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="subscripciones")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo_subscripcion")
@NoArgsConstructor
public abstract class ObservadorSuscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_humana")
    protected Humana humano;

    @OneToOne
    @JoinColumn(name = "id_heladera")
    protected Heladera heladera;

    ObservadorSuscripcion(Humana humano, Heladera heladera) {
        this.humano = humano;
        this.heladera = heladera;
    }
    abstract boolean condicion (Heladera heladera);

    abstract Mensaje mensaje(Heladera heladera);

    public void serNotificadoPor(Heladera heladera) {
        if (condicion(heladera)) {
            Contacto contactoHumano = humano.getMediosDeContactos().get(0);
            Notificador.notificar(mensaje(heladera), contactoHumano);
        }
    }
}
