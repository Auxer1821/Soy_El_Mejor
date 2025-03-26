package domain.usuario.tecnico.cobertura;

import domain.ubicaciones.Direccion;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("radio")
public class RadioCobertura extends TipoCobertura {
    private Double radio;
    private Direccion direccionCentro;

    public RadioCobertura(Double radio, Direccion direccionCentro) {
        this.radio = radio;
        this.direccionCentro = direccionCentro;
    }

    @Override
    public final Boolean cubre(Direccion direccionDestino) {

        Double result = direccionDestino.getCoordenadas().calcularDistancia(direccionCentro.getCoordenadas());
        return result < radio;
    }
}
