package services.mapView;

import Converters.HeladeraConverter;
import controllers.DTOs.heladera.HeladeraDTO;
import repositorios.heladera.RepoHeladera;

import java.util.List;

public class MapViewService {
    private final RepoHeladera repoHeladera;

    public MapViewService(RepoHeladera repoHeladera) {
        this.repoHeladera = repoHeladera;
    }

    public List<HeladeraDTO> obtener_heladeras() {
        return HeladeraConverter.convert(repoHeladera.buscarTodas());
    }

    public List<HeladeraDTO> obtener_heladeras_disponibles() {
        return HeladeraConverter.convert(repoHeladera.buscarHeladerasEnFuncionamientoParaDonar());
    }
}


