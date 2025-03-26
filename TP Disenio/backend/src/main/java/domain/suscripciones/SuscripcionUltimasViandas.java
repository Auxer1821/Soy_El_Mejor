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
@DiscriminatorValue("ultimasViandas")
@NoArgsConstructor
public class SuscripcionUltimasViandas extends ObservadorSuscripcion {
    @Column(name = "cantidadViandasMinima")
    private Integer cantidadViandasMinima;

    public SuscripcionUltimasViandas (Humana humana, Heladera heladera, Integer cantidadViandasMinima) {
        super(humana, heladera);
        this.cantidadViandasMinima = cantidadViandasMinima;
    }

    @Override
    boolean condicion(Heladera heladera) {
        return (this.cantidadViandasMinima <= heladera.cantViandasActual());
    }

    @Override
    Mensaje mensaje(Heladera heladera) {
        StringBuilder cuerpoMensaje = new StringBuilder();
        cuerpoMensaje.append("Ultimas viandas en heladera: ").append(heladera.getNombre()).
                append("\nUbicación: ").append(heladera.getDireccion())
                .append("\nCantidad viandas: ").append(heladera.cantViandasActual());
        return new Mensaje(cuerpoMensaje.toString(), "Desperfecto");
    }
}
