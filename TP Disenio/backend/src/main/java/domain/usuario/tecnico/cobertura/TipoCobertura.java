package domain.usuario.tecnico.cobertura;

import domain.ubicaciones.Direccion;

import javax.persistence.*;

@Embeddable
@DiscriminatorColumn(name = "tipo_cobertura")
public abstract class TipoCobertura {
    public Boolean cubre(Direccion direccionDestino) {
        return null;
    }
}

