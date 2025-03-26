package controllers.DTOs.tecnicos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitasDTO {
    private String descripcion;
    private String estado;
    private String fecha;
    private String foto_path;
    private String tecnico_cuil;
}