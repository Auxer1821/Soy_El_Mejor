package controllers.DTOs.suscripciones;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class SuscripcionDTO {
    private String humanaId;
    private String heladeraId;
    private List<String> suscripciones;
    private String cantidadViandas;

    public SuscripcionDTO(String humanaId, String heladeraId, List<String> suscripciones, String cantidadViandas) {
        this.humanaId = humanaId;
        this.heladeraId = heladeraId;
        this.suscripciones = suscripciones;
        this.cantidadViandas = cantidadViandas;
    }
}
