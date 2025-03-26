package domain.ubicaciones;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Localidad {
    @Column(name = "CP")
    private String codigoPostal;

    @Column(name="nombre_localidad")
    private String nombre;

    public Localidad(String nombre, String codigoPostal) {
        this.nombre = nombre;
        this.codigoPostal = codigoPostal;
    }

    public Localidad(String nombre) {
        this.nombre = nombre;
    }
}
