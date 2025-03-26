package domain.buscadorpuntosclaves;

import domain.ubicaciones.Coordenadas;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
@Getter
@Setter
public class ApiRestAdapter implements PuntoClaveAdapter{
    private APIREST api;

    @Override
    public Set<Coordenadas> puntosClaves(Coordenadas coordenada, Float radio) {
        return null;
    }
}
