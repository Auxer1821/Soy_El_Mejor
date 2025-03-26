package controllers.DTOs.heladera;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModeloDTO {
    private String nombre;

    private Double temperaturaMinima;

    private Double temperaturaMaxima;

    private Integer maxCantidadViandas;

}
