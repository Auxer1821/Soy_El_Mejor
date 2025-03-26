package domain.suscripciones;

import domain.comunicaciones.Mensaje;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.usuario.colaborador.Humana;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("desperfecto")
@NoArgsConstructor
public class SuscripcionDesperfecto extends ObservadorSuscripcion {
    public SuscripcionDesperfecto (Humana humana, Heladera heladera)
    {
        super(humana, heladera);
        setHeladera(heladera);
    }

    @Override
    boolean condicion(Heladera heladera) {
        return heladera.getEstado() == Estado.INACTIVA;
    }

    @Override
    Mensaje mensaje(Heladera heladera) {
        StringBuilder cuerpoMensaje = new StringBuilder();
        cuerpoMensaje.append("Desperfecto en heladera: ").append(heladera.getNombre()).
                append("\nUbicación: ").append(heladera.getDireccion());
        return new Mensaje(cuerpoMensaje.toString(), "Desperfecto");
    }
}
