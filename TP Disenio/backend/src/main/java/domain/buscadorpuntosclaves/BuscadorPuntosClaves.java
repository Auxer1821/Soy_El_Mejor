package domain.buscadorpuntosclaves;
import domain.ubicaciones.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class BuscadorPuntosClaves {
    private PuntoClaveAdapter buscador;

    public Set<Coordenadas> puntosClaves(Direccion direccion, Float radio){
        return buscador.puntosClaves(direccion.getCoordenadas(),radio);
    }
}
