package domain.usuario.tecnico.buscadorTecnicoCercano;

import domain.ubicaciones.Coordenadas;
import domain.usuario.tecnico.Tecnico;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BuscadorTecnicoCercano {

    public static Optional<Tecnico> buscarTecnicoCercano (List<Tecnico> tecnicos, Coordenadas coordenadas) {
        List<Coordenadas> ubicaciones = tecnicos.stream()
                .map(Tecnico::getUbicacion)
                .collect(Collectors.toList());

        Coordenadas coordenadaCercana = coordenadas.calcularPuntoMasCercano((ArrayList<Coordenadas>) ubicaciones);

        Optional<Tecnico> tecnico_cercano = tecnicos.stream()
                .filter(tecnico -> tecnico.getUbicacion().equals(coordenadaCercana))
                .findFirst();

        return tecnico_cercano;
    }
}
