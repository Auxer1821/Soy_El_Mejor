package controllers.DTOs.heladera;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReporteFallaDTO {
    private String idHeladera;
    private String fecha;
    private String id_humana;
    private String descripcion;
    private String urlFoto;
}
