package domain.buscadorpuntosclaves;

import domain.ubicaciones.Coordenadas;

import java.util.Set;

public interface PuntoClaveAdapter {
    Set<Coordenadas> puntosClaves(Coordenadas coordenada, Float radio);
}
