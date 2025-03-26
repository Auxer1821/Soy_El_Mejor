package domain.reportes;

import domain.heladera.Heladera;
import domain.reportes.type.ReporteDonaciones;
import domain.reportes.type.ReporteFallas;
import domain.reportes.type.ReporteViandasPorColaborador;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReporteHeladera {
    private Heladera heladera;
    private ReporteDonaciones reporteDonaciones = new ReporteDonaciones();
    private ReporteFallas reporteFallas = new ReporteFallas();
    private ReporteViandasPorColaborador reporteViandasPorColaborador = new ReporteViandasPorColaborador();

    public ReporteHeladera(Heladera heladera) {
        this.heladera = heladera;
        this.reporteDonaciones.generarReporte(heladera);
        this.reporteFallas.generarReporte(heladera);
        this.reporteViandasPorColaborador.generarReporte(heladera);
    }
}

