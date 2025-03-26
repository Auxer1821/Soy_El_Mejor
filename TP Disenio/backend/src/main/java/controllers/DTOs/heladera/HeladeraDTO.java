package controllers.DTOs.heladera;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HeladeraDTO {
    private Double lat;
    private Double lon;
    private String name;
    private String id;
    private Integer cantidadMaxima;
    private Integer cantidadViandas;
    private String icono;
    private String estado;

    // Constructor
    public HeladeraDTO(Double lat, Double lon, String name, String id, Integer cantidadMaxima, Integer cantidadViandas, String estado) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.id = id;
        this.cantidadMaxima = cantidadMaxima;
        this.cantidadViandas = cantidadViandas;
        this.estado = estado;
    }
}
