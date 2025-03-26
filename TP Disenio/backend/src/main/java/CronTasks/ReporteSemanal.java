package CronTasks;

import config.ServiceLocator;
import domain.heladera.Heladera;
import domain.reportes.ReporteGeneral;
import domain.reportes.ReporteHeladera;
import domain.reportes.estrategias.GenerarReportePDF;
import org.eclipse.paho.client.mqttv3.MqttException;
import repositorios.RepoGenerico;
import repositorios.heladera.RepoHeladera;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ReporteSemanal {
    public static void main(String[] args) throws FileNotFoundException, MqttException, InterruptedException {
        RepoGenerico repoGenerico = new RepoGenerico();

        List<ReporteHeladera> reporteHeladeras = new ArrayList<>();

        List<Heladera> heladeras = ServiceLocator.instanceOf(RepoHeladera.class).buscarTodas();

        heladeras.forEach(heladera -> {
            ReporteHeladera reporteHeladera = new ReporteHeladera(heladera);

            reporteHeladeras.add(reporteHeladera);
        });

        GenerarReportePDF reporteSemanal = new GenerarReportePDF();
        ReporteGeneral reporteGeneral = reporteSemanal.generarReportePDF(reporteHeladeras);

        repoGenerico.beginTransaction();
        repoGenerico.persist(reporteGeneral);
        repoGenerico.commitTransaction();
    }
}
