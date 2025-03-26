package domain.reportes.type;

import config.ServiceLocator;
import domain.heladera.Heladera;
import lombok.Getter;
import lombok.Setter;
import repositorios.heladera.RepoMovimientos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Setter
@Getter
public class ReporteFallas {
    private Integer cantidadIncidentes;

    public void generarReporte(Heladera heladera) {
        LocalDateTime haceUnaSemana = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        cantidadIncidentes = ServiceLocator.instanceOf(RepoMovimientos.class).incidentes(heladera).stream()
                .filter(incidente -> incidente.getFecha_incidente().isAfter(haceUnaSemana))
                .toList().size();
    }
}
