package services.sensores;

import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.incidente.EstadoIncidente;
import domain.heladera.incidente.Incidente;
import domain.heladera.incidente.IncidenteFactory;
import domain.heladera.incidente.TipoIncidente;
import domain.heladera.sensores.SensorMovimiento;
import domain.heladera.sensores.datosRecibidos.AlertaMovimiento;
import lombok.Getter;
import repositorios.RepoGenerico;
import repositorios.heladera.RepoHeladera;

import java.time.LocalDateTime;

@Getter
public class SensorMovimientoService {
    private RepoGenerico repoGenerico;
    private RepoHeladera repoHeladera;

    public SensorMovimientoService(RepoGenerico repoGenerico, RepoHeladera repoHeladera) {
        this.repoGenerico = repoGenerico;
        this.repoHeladera = repoHeladera;
    }

    public void registrarAlertaMovimiento(String alerta, SensorMovimiento sensorMovimiento){
        AlertaMovimiento alertaMovimiento = new AlertaMovimiento(alerta, sensorMovimiento, LocalDateTime.now());

        sensorMovimiento.registrarAlerta(alertaMovimiento);

        repoHeladera.modificarEstado(sensorMovimiento.getHeladera().getId(), Estado.INACTIVA);

        Incidente incidente = IncidenteFactory.create(TipoIncidente.FRAUDE, sensorMovimiento.getHeladera(), EstadoIncidente.NO_SOLUCIONADO);

        try{
            repoGenerico.beginTransaction();
            repoGenerico.persist(alertaMovimiento);
            repoGenerico.persist(incidente);
            repoGenerico.commitTransaction();
        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw e;
        }
    }

}
