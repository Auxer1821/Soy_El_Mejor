package controllers.controladores.fallas;

import controllers.DTOs.heladera.ReporteFallaDTO;
import domain.heladera.incidente.Incidente;
import services.ReportarFallaService;

public class ReportarFallaController {
    private ReportarFallaService fallaService;



    public Incidente registrarIncidente(ReporteFallaDTO reporte){
        return fallaService.registrarIncidente(reporte);
    }
}
