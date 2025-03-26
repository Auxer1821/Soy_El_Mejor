package domain.ubicaciones.comunidades;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Comunidades")
@NoArgsConstructor
public class Comunidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    public Comunidad(String nombre, String direccion){
        this.nombre = nombre;
        this.direccion = direccion;
    }
}
