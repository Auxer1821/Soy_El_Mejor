package services.heladera;

import domain.heladera.Heladera;
import domain.heladera.sensores.SensorPersistente;
import repositorios.RepoGenerico;
import repositorios.colaboraciones.RepoColaboraciones;
import repositorios.heladera.RepoHeladera;

import java.util.List;


public class HeladeraService {
    private RepoHeladera repoHeladera;
    private RepoGenerico repoGenerico;
    private RepoColaboraciones repoColaboraciones;

    public HeladeraService(RepoHeladera repoHeladera, RepoGenerico repoGenerico, RepoColaboraciones repoColaboraciones) {
        this.repoHeladera = repoHeladera;
        this.repoGenerico = repoGenerico;
        this.repoColaboraciones = repoColaboraciones;
    }

    public Heladera buscarHeladeraPorNombre(String nombre){
        return repoHeladera.buscarPorNombre(nombre);
    }

    public void eliminarHeladeraConID(String id) {
        repoHeladera.eliminarHeladeraPorId(id);
    }

    public List<Heladera> buscarHeladerasActivas() {
        return repoHeladera.buscarHeladerasActivas();
    }

    public List<SensorPersistente> buscarSensoresHeladera(Heladera heladera) {
        return repoHeladera.buscarSensoresHeladera(heladera.getId());
    }

   public void editarHeladeraPorId(Heladera heladera) {
        repoHeladera.editarHeladeraPorId(heladera);
   }
}
