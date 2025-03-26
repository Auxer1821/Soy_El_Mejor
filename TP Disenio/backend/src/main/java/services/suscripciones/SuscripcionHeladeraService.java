package services.suscripciones;

import controllers.DTOs.suscripciones.SuscripcionDTO;
import domain.heladera.Heladera;
import domain.suscripciones.SuscripcionDesperfecto;
import domain.suscripciones.SuscripcionUltimasViandas;
import domain.suscripciones.SuscripcionViandasFaltantes;
import domain.usuario.colaborador.Humana;
import repositorios.colaboradores.RepoHumana;
import repositorios.heladera.RepoHeladera;

public class SuscripcionHeladeraService {
    private RepoHeladera repositorioHeladeras;
    private RepoHumana repositorioHumana;

    public SuscripcionHeladeraService(RepoHeladera repoHeladera, RepoHumana repoHumana) {
        this.repositorioHeladeras = repoHeladera;
        this.repositorioHumana = repoHumana;
    }

        public void realizar_suscripcion(SuscripcionDTO suscripcionDTO) {
            try {
                Humana humana = repositorioHumana.buscarPorID(suscripcionDTO.getHumanaId());
                Heladera heladera = repositorioHeladeras.buscarPorId(suscripcionDTO.getHeladeraId());
                Integer cantidadViandas = Integer.valueOf(suscripcionDTO.getCantidadViandas());

                repositorioHeladeras.beginTransaction();
                for (String suscripcion : suscripcionDTO.getSuscripciones()) {
                    switch (suscripcion) {
                        case "avisoVacio":
                            SuscripcionViandasFaltantes suscripcionVaciamiento = new SuscripcionViandasFaltantes(humana, heladera, cantidadViandas);
                            repositorioHeladeras.persist(suscripcionVaciamiento);
                            break;
                        case "avisoDesperfecto":
                            SuscripcionDesperfecto suscripcionDesperfecto = new SuscripcionDesperfecto(humana, heladera);
                            repositorioHeladeras.persist(suscripcionDesperfecto);
                            break;
                        case "avisoLleno":
                            SuscripcionUltimasViandas suscripcionUltimasViandas = new SuscripcionUltimasViandas(humana, heladera, cantidadViandas);
                            repositorioHeladeras.persist(suscripcionUltimasViandas);
                            break;
                    }
                }
                repositorioHeladeras.commitTransaction();
            } catch (Exception e) {
                repositorioHeladeras.rollbackTransaction();
                throw new RuntimeException(e.getMessage());
            }
        }
    }
