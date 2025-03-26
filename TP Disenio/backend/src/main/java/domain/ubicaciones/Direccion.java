package domain.ubicaciones;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Direccion {
    @Column(name="Direccion", nullable = false)
    private String direccion;

    @Embedded
    private Localidad localidad;

    @Column
    private String provincia;

    @Embedded
    private Coordenadas coordenadas;

    public Direccion(String direccion, Localidad localidad, String provincia, Coordenadas coordenadas) {
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
        this.coordenadas = coordenadas;
    }

    public Direccion(String direccion, Localidad localidad, String provincia) {
        this.direccion = direccion;
        this.localidad = localidad;
        this.provincia = provincia;
    }

    public Direccion(String direccion) {
        this.direccion = direccion;
    }
}
