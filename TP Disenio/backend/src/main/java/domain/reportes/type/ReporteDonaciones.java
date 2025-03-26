package domain.reportes.type;

import config.ServiceLocator;
import domain.heladera.Heladera;
import domain.heladera.entradaSalida.EntradaSalida;
import lombok.Getter;
import lombok.Setter;
import repositorios.heladera.RepoMovimientos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Setter
@Getter
public class ReporteDonaciones {
    private Integer cantidadEntradas = 0;
    private Integer cantidadSalidas = 0;

    public void registrarTransacciones(List<EntradaSalida> ultimasES) {
       ultimasES.forEach(entradaSalida -> {
                    switch(entradaSalida.getMotivo()) {
                        case ENTRADA:
                            cantidadEntradas += entradaSalida.getCantidadViandas();
                            break;
                        case SALIDA:
                            cantidadSalidas += entradaSalida.getCantidadViandas();
                            break;
                    }
                });
    }

    public void generarReporte(Heladera heladera){
        LocalDateTime haceUnaSemana = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        List<EntradaSalida> ESUltimaSemana = ServiceLocator.instanceOf(RepoMovimientos.class)
                .entradaSalidaList(heladera)
                .stream()
                .filter(entradaSalida -> entradaSalida.getFechaMovimiento().isAfter(haceUnaSemana)).toList();

        registrarTransacciones(ESUltimaSemana);
    }
}
