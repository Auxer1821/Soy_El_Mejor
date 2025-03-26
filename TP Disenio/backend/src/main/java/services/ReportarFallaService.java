package services;

import controllers.DTOs.heladera.ReporteFallaDTO;
import domain.heladera.Heladera;
import domain.heladera.incidente.Incidente;
import domain.heladera.incidente.IncidenteFactory;
import domain.heladera.incidente.TipoIncidente;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import repositorios.RepoGenerico;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.heladera.RepoHeladera;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class ReportarFallaService {
    private RepoHeladera repoHeladera;
    private RepoHumana repoHumana;
    private RepoJuridica repoJuridica;
    private RepoGenerico repositorio;

    public Incidente registrarIncidente(ReporteFallaDTO reporte) {
        Incidente incidente = null;

        Heladera heladera = repoHeladera.buscarPorId(reporte.getIdHeladera());

        LocalDate fechaFalla = LocalDate.parse(reporte.getFecha(), DateTimeFormatter.ISO_LOCAL_DATE);

        if (reporte.getId_humana().isEmpty()) {
            Humana humana = repoHumana.buscarPorID(reporte.getId_humana());
            if (heladera != null && humana != null) {
                incidente = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA, heladera, fechaFalla.atStartOfDay(), humana);
                repositorio.persist(incidente);
            }
        } else {
            throw new RuntimeException("No se proporciono colaborador al cual atribuirle el reporte");
        }

        return incidente;
    }
}
