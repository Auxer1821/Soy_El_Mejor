package domain.usuario.tecnico.cobertura;
import domain.ubicaciones.Direccion;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;


@DiscriminatorValue("zona")
public class ZonaCobertura extends TipoCobertura {
    @Column(name = "CPs")
    private String codigosPostales;

    public Boolean cubre(Direccion direccionDestino) {
        return null;
    }
}
