package domain.reportes.type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CantidadViandasPorColaborador {
    private String Id;
    private String nombreColaborador;
    private Integer cantidadDeViandas = 1;

    public CantidadViandasPorColaborador(String Id, String nombreColaborador) {
        this.nombreColaborador = nombreColaborador;
        this.Id = Id;
    }

    public void incrementarCantidad() {
        this.cantidadDeViandas++;
    }
}
