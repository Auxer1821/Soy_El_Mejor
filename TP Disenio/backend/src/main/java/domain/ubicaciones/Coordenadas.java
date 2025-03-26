package domain.ubicaciones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Coordenadas {
    private Double latitud;
    private Double longitud;

    public Coordenadas(Double latitude, Double longitude) {
        this.latitud = latitude;
        this.longitud = longitude;
    }

    public double calcularDistancia(Coordenadas punto) {
        double dLat = Math.toRadians(this.getLatitud() - punto.getLatitud());
        double dLon = Math.toRadians(this.getLongitud() - punto.getLongitud());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(this.getLatitud())) * Math.cos(Math.toRadians(punto.getLongitud())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c;
    }

    public Coordenadas calcularPuntoMasCercano(ArrayList<Coordenadas> puntos) {
        return puntos.stream()
                .min(Comparator.comparingDouble(this::calcularDistancia))
                .orElse(null);
    }
}
