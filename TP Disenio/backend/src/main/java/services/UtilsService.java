package services;

import Converters.HeladeraConverter;
import config.ServiceLocator;
import controllers.DTOs.heladera.IncidenteDTO;
import controllers.DTOs.otros.ReporteDTO;
import controllers.DTOs.tecnicos.VisitasDTO;
import domain.broker.transmisores.Asunto;
import domain.broker.transmisores.TransmisorMQTT;
import domain.heladera.Heladera;
import domain.heladera.incidente.Incidente;
import domain.heladera.sensores.SensorTemperatura;
import domain.reportes.ReporteGeneral;
import domain.reportes.ReporteHeladera;
import domain.reportes.estrategias.GenerarReportePDF;
import domain.tarjetas.Tarjeta;
import domain.usuario.colaborador.Humana;
import io.javalin.http.UploadedFile;
import org.eclipse.paho.client.mqttv3.MqttException;
import repositorios.colaboradores.RepoHumana;
import repositorios.heladera.RepoHeladera;
import repositorios.heladera.RepoMovimientos;
import repositorios.tarjetas.RepoTarjetas;
import utils.Varios.NumeroRandom;
import utils.csv.ReaderCSV;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UtilsService {
    private RepoHeladera repoHeladera;

    public UtilsService(RepoHeladera repoHeladera) {
        this.repoHeladera = repoHeladera;
    }

    public void realizarMigracion(UploadedFile archivo) {
        String directory = "docs/Otros/Migraciones";
        Path destinationDirectory = Paths.get(directory);

        try {
            String sanitizedFilename = archivo.filename().replaceAll("\\s+", "_");
            Path destination = destinationDirectory.resolve(sanitizedFilename);

            OutputStream outputStream;

            if (Files.exists(destination)) {
                String newFilename = System.currentTimeMillis() + "_" + sanitizedFilename;
                destination = destinationDirectory.resolve(newFilename);

                outputStream = Files.newOutputStream(destination);

                archivo.content().transferTo(outputStream);
            } else {
                outputStream = Files.newOutputStream(destination);
                archivo.content().transferTo(outputStream);
            }

            ReaderCSV.readColaboracionCSV(destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void simular_temperaruta() throws UnknownHostException, MqttException {
        NumeroRandom generadorNumerosRandom = new NumeroRandom();
        RepoHeladera repoHeladera = ServiceLocator.instanceOf(RepoHeladera.class);

        TransmisorMQTT transmisorTemperaturaMQTT = new TransmisorMQTT();

        Heladera heladera = repoHeladera.buscarPorNombre("Heladera UTN - CABA");

        //heladeras.forEach(heladera -> {
        SensorTemperatura sensorTemperatura = repoHeladera.buscarSensorTemperatura(heladera.getId());
        int temperaturaHeladera = generadorNumerosRandom.getRandomNumberBetween(20, 30);

        try {
            transmisorTemperaturaMQTT.enviarMensaje(String.valueOf(sensorTemperatura.getHeladera().getId()), String.valueOf(temperaturaHeladera), Asunto.TEMPERATURA);

        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        //});

        System.out.println("Se envio la temperatura satisfactoriamente");
    }

    public void simular_tarjeta() throws UnknownHostException, MqttException {
        RepoHumana repoHumana = ServiceLocator.instanceOf(RepoHumana.class);
        RepoTarjetas repoTarjetas = ServiceLocator.instanceOf(RepoTarjetas.class);
        RepoHeladera repoHeladera = ServiceLocator.instanceOf(RepoHeladera.class);

        Humana humana = repoHumana.buscarPorUsername("luiscastro");
        Tarjeta tarjeta = repoTarjetas.buscarPorIdHumana(humana.getId());
        Heladera heladera = repoHeladera.buscarPorNombre("Heladera UTN - CABA");

        TransmisorMQTT transmisorMQTT = new TransmisorMQTT();

        transmisorMQTT.enviarMensaje(heladera.getId(), tarjeta.getCodigo(), Asunto.APERTURA);
    }

    public List<ReporteDTO> buscar_todos_reportes() {
        return HeladeraConverter
                .convertirReportes(ServiceLocator.instanceOf(RepoMovimientos.class).reportes());
    }

    public List<ReporteDTO> buscar_reportes_entre(LocalDate fechaInicio, LocalDate fechaFin) {
        return HeladeraConverter
                .convertirReportes(ServiceLocator.instanceOf(RepoMovimientos.class).reportes_entre(fechaInicio, fechaFin));
    }

    public List<Incidente> buscar_incidentes_recientes() {
        return repoHeladera.getIncidentesUltSemana();
    }

    public List<Incidente> obtenerTodosLosIncidentes() {
        List<Incidente> incidentes = repoHeladera.getTodosLosIncidentes();
        return incidentes;
    }

    public void generar_reporte() throws FileNotFoundException {
        List<ReporteHeladera> reporteHeladeras = new ArrayList<>();
        List<Heladera> heladeras = ServiceLocator.instanceOf(RepoHeladera.class).buscarTodas();

        heladeras.forEach(heladera -> {
            ReporteHeladera reporteHeladera = new ReporteHeladera(heladera);

            reporteHeladeras.add(reporteHeladera);
        });

        GenerarReportePDF reporteSemanal = new GenerarReportePDF();
        ReporteGeneral reporteGeneral = reporteSemanal.generarReportePDF(reporteHeladeras);

        repoHeladera.beginTransaction();
        repoHeladera.persist(reporteGeneral);
        repoHeladera.commitTransaction();
    }
}
