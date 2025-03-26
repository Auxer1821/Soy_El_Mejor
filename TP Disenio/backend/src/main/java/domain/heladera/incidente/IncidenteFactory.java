package domain.heladera.incidente;

import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;

import java.time.LocalDateTime;

public class IncidenteFactory {
    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, EstadoIncidente estadoIncidente) {
        Incidente incidente = new Incidente(tipoIncidente, heladera, LocalDateTime.now());
        incidente.setEstado(estadoIncidente);
        return incidente;
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, LocalDateTime fecha) {
        return new Incidente(tipoIncidente, heladera, fecha);
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, LocalDateTime fecha, Humana humana) {
        Incidente incidente = create(tipoIncidente, heladera, fecha);
        incidente.setHumana(humana);
        return incidente;
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, Humana humana) {
        Incidente incidente = create(tipoIncidente, heladera, EstadoIncidente.NO_SOLUCIONADO);
        incidente.setHumana(humana);
        return incidente;
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, LocalDateTime fecha, Humana humana, String fotoUrl, String descripcion) {
        Incidente incidente = create(tipoIncidente, heladera, fecha, humana);
        incidente.setUrlFoto(fotoUrl);
        incidente.setDescripcion(descripcion);
        return incidente;
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, Humana humana, String fotoUrl, String descripcion) {
        Incidente incidente = create(tipoIncidente, heladera, humana);
        incidente.setUrlFoto(fotoUrl);
        incidente.setDescripcion(descripcion);
        return incidente;
    }


    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, LocalDateTime fecha, String fotoUrl, String descripcion) {
        Incidente incidente = create(tipoIncidente, heladera, fecha);
        incidente.setUrlFoto(fotoUrl);
        incidente.setDescripcion(descripcion);
        return incidente;
    }

    public static Incidente create(TipoIncidente tipoIncidente, Heladera heladera, String fotoUrl, String descripcion) {
        Incidente incidente = create(tipoIncidente, heladera, EstadoIncidente.NO_SOLUCIONADO);
        incidente.setUrlFoto(fotoUrl);
        incidente.setDescripcion(descripcion);
        return incidente;
    }
}
