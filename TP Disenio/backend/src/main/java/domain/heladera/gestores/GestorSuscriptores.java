package domain.heladera.gestores;

import domain.heladera.Heladera;
import domain.suscripciones.ObservadorSuscripcion;
import domain.suscripciones.SuscripcionDesperfecto;
import domain.suscripciones.SuscripcionUltimasViandas;
import domain.suscripciones.SuscripcionViandasFaltantes;
import jdk.jfr.Enabled;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Embeddable
public class GestorSuscriptores {
    @OneToMany
    @JoinColumn(name = "id_heladera")
    private List<SuscripcionDesperfecto> suscripcionDesperfecto;

    @OneToMany
    @JoinColumn(name = "id_heladera")
    private List<SuscripcionUltimasViandas> suscripcionUltimasViandas;

    @OneToMany
    @JoinColumn(name = "id_heladera")
    private List<SuscripcionViandasFaltantes> suscripcionViandasFaltantes;

    public GestorSuscriptores() {
        this.suscripcionDesperfecto = new ArrayList<>();
        this.suscripcionUltimasViandas = new ArrayList<>();
        this.suscripcionViandasFaltantes = new ArrayList<>();
    }

    // Métodos para agregar suscriptores
    public void agregarSuscriptorDesperfecto(SuscripcionDesperfecto suscriptor) {
        suscripcionDesperfecto.add(suscriptor);
    }

    public void agregarSuscriptorUltimasViandas(SuscripcionUltimasViandas suscriptor) {
        suscripcionUltimasViandas.add(suscriptor);
    }

    public void agregarSuscriptorViandasFaltantes(SuscripcionViandasFaltantes suscriptor) {
        suscripcionViandasFaltantes.add(suscriptor);
    }

    // Métodos para quitar suscriptores
    public void quitarSuscriptorDesperfecto(SuscripcionDesperfecto suscriptor) {
        suscripcionDesperfecto.remove(suscriptor);
    }

    public void quitarSuscriptorUltimasViandas(SuscripcionUltimasViandas suscriptor) {
        suscripcionUltimasViandas.remove(suscriptor);
    }

    public void quitarSuscriptorViandasFaltantes(SuscripcionViandasFaltantes suscriptor) {
        suscripcionViandasFaltantes.remove(suscriptor);
    }

    // Métodos para notificar suscriptores
    public void notificarDeperfecto(Heladera heladera) {
        suscripcionDesperfecto.forEach(suscriptor -> suscriptor.serNotificadoPor(heladera));
    }

    public void notificarUltimasViandas(Heladera heladera) {
        suscripcionUltimasViandas.forEach(suscriptor -> suscriptor.serNotificadoPor(heladera));
    }

    public void notificarViandasFaltantes(Heladera heladera) {
        suscripcionViandasFaltantes.forEach(suscriptor -> suscriptor.serNotificadoPor(heladera));
    }
}
