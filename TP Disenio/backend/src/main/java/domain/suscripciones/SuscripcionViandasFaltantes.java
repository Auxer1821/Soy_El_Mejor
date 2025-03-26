package domain.suscripciones;

import domain.comunicaciones.Mensaje;
import domain.heladera.Heladera;
import domain.usuario.colaborador.Humana;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Entity
@DiscriminatorValue("viandasFaltantes")
@NoArgsConstructor
public class SuscripcionViandasFaltantes extends ObservadorSuscripcion {
    @Column(name = "cantidadViandasMaxima")
    private Integer cantidadViandasMaxima;

    public SuscripcionViandasFaltantes (Humana humana, Heladera heladera, Integer cantidadViandasMaxima) {
        super(humana, heladera);
        this.cantidadViandasMaxima = cantidadViandasMaxima;
    }

    @Override
    boolean condicion(Heladera heladera) {
        return (this.cantidadViandasMaxima <= heladera.cantViandasActual());
    }

    @Override
    Mensaje mensaje(Heladera heladera) {
        StringBuilder cuerpoMensaje = new StringBuilder();
        cuerpoMensaje.append("Viandas faltantes en heladera: ").append(heladera.getNombre()).
                append("\nUbicación: ").append(heladera.getDireccion()).append(" ").append(heladera.getDireccion())
                .append("\nCantidad viandas faltantes: ").append(heladera.getMaxCantidadViandas() - heladera.cantViandasActual());
        return new Mensaje(cuerpoMensaje.toString(), "Desperfecto");
    }
}
