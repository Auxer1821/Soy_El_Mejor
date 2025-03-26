package controllers.DTOs.otros;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComunidadDTO {
    private String nombre_comunidad;

    private String direccion;
}
