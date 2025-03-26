package controllers.DTOs.heladera;

import controllers.DTOs.tecnicos.VisitasDTO;
import domain.heladera.incidente.Incidente;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncidenteDTO {
    private String heladera_nombre;

    private String heladera_direccion;

    private String tipoIncidente;

    private String urlFoto;

    private String descripcion;

    private LocalDateTime fecha;

    private List<VisitasDTO> visitas;
}




