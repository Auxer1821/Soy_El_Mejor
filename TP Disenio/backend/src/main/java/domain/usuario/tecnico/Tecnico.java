package domain.usuario.tecnico;

import domain.comunicaciones.Contacto;
import domain.identificador.Documento;
import domain.ubicaciones.Coordenadas;
import domain.usuario.tecnico.cobertura.TipoCobertura;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tecnicos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Tecnico {
    @Id
    private String cuil;

    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Embedded
    private Documento documento;

    @OneToOne(mappedBy = "tecnico")
    private Contacto contacto;

    @Column(name = "habilitado", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean habilitado;

    @Embedded
    private TipoCobertura cobertura;

    @Embedded
    private Coordenadas ubicacion;

    public Tecnico(String cuil, String nombre, String apellido, Documento documento, TipoCobertura cobertura, Coordenadas ubicacion) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.cobertura = cobertura;
        this.ubicacion = ubicacion;
        this.habilitado = true;
    }

    public Tecnico(String cuil, String nombre, String apellido, Documento documento, Coordenadas ubicacion) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.habilitado = true;
        this.ubicacion = ubicacion;
    }
}
