package services.sensores;


import config.ServiceLocator;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.incidente.EstadoIncidente;
import domain.heladera.incidente.Incidente;
import domain.heladera.incidente.IncidenteFactory;
import domain.heladera.incidente.TipoIncidente;
import domain.heladera.sensores.SensorTemperatura;
import domain.heladera.sensores.datosRecibidos.MedicionTemperatura;
import domain.usuario.tecnico.Tecnico;
import repositorios.RepoGenerico;
import repositorios.heladera.RepoHeladera;
import services.notificadores.NotificadorDeTecnicos;

import java.time.LocalDateTime;
import java.util.Optional;

public class SensorTemperaturaService {
    private RepoGenerico repoGenerico;
    private RepoHeladera repoHeladera;

    public SensorTemperaturaService(RepoGenerico repoGenerico, RepoHeladera repoHeladera) {
        this.repoGenerico = repoGenerico;
        this.repoHeladera = repoHeladera;
    }

    public void setearMedicionActual(Double medicion, SensorTemperatura sensorTemperatura) {
        MedicionTemperatura nueva_medicion = new MedicionTemperatura(LocalDateTime.now(), sensorTemperatura, medicion);
        sensorTemperatura.registrarMedicion(nueva_medicion);

        Heladera heladera = repoHeladera.buscarPorId(sensorTemperatura.getHeladera().getId());

        heladera.setTemperaturaActual(medicion);

        repoGenerico.beginTransaction();
        if(!heladera.temperaturaEntreLimites()){
            heladera.setEstado(Estado.INACTIVA);

            Incidente incidente = IncidenteFactory.create(TipoIncidente.TEMPERATURA, heladera, EstadoIncidente.NO_SOLUCIONADO);

            heladera.registrarIncidente(incidente);
            Optional<Tecnico> tecnico = ServiceLocator.instanceOf(NotificadorDeTecnicos.class).notificarTecnicoMasCercano(heladera);

            if(tecnico.isPresent()){
                incidente.setTecnico_asignado(tecnico.get());
            }else {
                repoGenerico.rollbackTransaction();
                throw new RuntimeException("No se encontro ningun tecnico cercano para cubrir el incidente");
            }

            repoHeladera.modificarTemperaturaYEstado(heladera.getId(), medicion, Estado.INACTIVA);
            repoGenerico.persist(incidente);
        }else {
            repoHeladera.modificarTemperatura(heladera.getId(), medicion);
        }

        repoGenerico.persist(nueva_medicion);
        repoGenerico.commitTransaction();
    }
}
