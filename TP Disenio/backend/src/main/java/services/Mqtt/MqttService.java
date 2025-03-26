package services.Mqtt;

import domain.colaboraciones.DistribucionViandas;
import domain.colaboraciones.DonacionVianda;
import domain.colaboraciones.SolicitudApertura;
import domain.heladera.AperturaFehaciente;
import domain.heladera.Heladera;
import domain.sistemaPuntuacion.ColaboracionRealizada;
import domain.sistemaPuntuacion.MotivoColaboracionRealizada;
import domain.tarjetas.Tarjeta;
import domain.tarjetas.Usos;
import domain.usuario.colaborador.Humana;
import repositorios.colaboraciones.RepoColaboraciones;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.heladera.RepoHeladera;
import repositorios.tarjetas.RepoSolicitudesApertura;
import repositorios.tarjetas.RepoTarjetas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class MqttService {
    private RepoSolicitudesApertura repoSolicitudes;
    private RepoColaboraciones repoColaboraciones;
    private RepoTarjetas repoTarjetas;
    private RepoHeladera repoHeladera;

    public MqttService(RepoSolicitudesApertura repoSolicitudes, RepoColaboraciones repoColaboraciones,
            RepoTarjetas repoTarjetas, RepoHeladera repoHeladera) {
        this.repoSolicitudes = repoSolicitudes;
        this.repoColaboraciones = repoColaboraciones;
        this.repoTarjetas = repoTarjetas;
        this.repoHeladera = repoHeladera;
    }

    public void puedeAbrir(String id_heladera, String codigo_tarjeta) {
        repoColaboraciones.beginTransaction();
        try {
            Tarjeta tarjeta = repoTarjetas.buscarPorID(codigo_tarjeta);
            Heladera heladera = repoHeladera.buscarPorId(id_heladera);
            Humana humana = tarjeta.getPersonaHumana();

            List<SolicitudApertura> solicitudAperturas = repoSolicitudes.obtenerSolicitudes(codigo_tarjeta,
                    heladera.getId());

            solicitudAperturas.forEach(heladera::registrarSolicitudApertura);

            SolicitudApertura solicitudApertura = heladera.puedeAbrir(tarjeta).get(0);

            if (solicitudApertura.getDistribucion() != null) {
                Optional<DistribucionViandas> distribucionViandas = repoColaboraciones
                        .buscarPorId(solicitudApertura.getDistribucion().getId());

                if (distribucionViandas.isPresent()
                        && distribucionViandas.get().getHeladeraDestino().getId().startsWith(heladera.getId())) {
                    repoColaboraciones.modificarDistribucion(distribucionViandas.get().getId());

                    ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada(
                            MotivoColaboracionRealizada.DISTRIBUCIONVIANDA, distribucionViandas.get());

                    repoColaboraciones.persist(colaboracionRealizada);
                    humana.setPuntos_adquiridos(humana.getPuntos_adquiridos() + colaboracionRealizada.puntosGanados());
                }

            } else {
                Optional<DonacionVianda> donacionVianda = repoColaboraciones
                        .findDVPorId(solicitudApertura.getDonacionVianda().getId());

                donacionVianda.ifPresent(donacion -> repoColaboraciones.modificarDonacionVianda(donacion.getId()));

                ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada(
                        MotivoColaboracionRealizada.DONACIONVIANDA, donacionVianda.get());
                repoColaboraciones.persist(colaboracionRealizada);
                humana.setPuntos_adquiridos(humana.getPuntos_adquiridos() + colaboracionRealizada.puntosGanados());
            }

            AperturaFehaciente apertura_fehaciente = new AperturaFehaciente(tarjeta.getPersonaHumana(), heladera,
                    LocalDateTime.now());
            heladera.registrarApertura(apertura_fehaciente);
            repoColaboraciones.persist(apertura_fehaciente);
            repoColaboraciones.persist(humana);

            Usos uso_nuevo = new Usos(LocalDate.now(), heladera, tarjeta);
            repoColaboraciones.persist(uso_nuevo);
            tarjeta.aniadirUsos(uso_nuevo);

            repoColaboraciones.commitTransaction();
        } catch (

        Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
