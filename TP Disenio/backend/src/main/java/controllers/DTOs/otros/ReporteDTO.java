package controllers.DTOs.otros;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReporteDTO {
    private String fecha;
    private String path;
}
