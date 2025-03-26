package domain.identificador;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Embeddable
public class Documento {
    @Column(name = "nro_documento")
    private String numero;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo;

    public Documento() {
    }

    public Documento (String numero, TipoDocumento tipo) {
        this.numero = numero;
        this.tipo = tipo;
    }
}
