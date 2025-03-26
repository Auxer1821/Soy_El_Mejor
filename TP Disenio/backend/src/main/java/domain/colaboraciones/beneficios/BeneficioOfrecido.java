package domain.colaboraciones.beneficios;

import domain.usuario.colaborador.Juridica;
import lombok.*;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "beneficios_ofrecidos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BeneficioOfrecido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoRubro rubro;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "puntos_necesarios", nullable = false)
    private Integer puntosNecesarios;

    @Column(name = "image_path")
    private String imagenPath;

    @ManyToOne
    @JoinColumn(name = "colaborador_id", nullable = false)
    private Juridica colaborador;

    public BeneficioOfrecido(String nombre, Integer puntosNecesarios, String imagenPath, Juridica colaborador, String descripcion) {
        this.nombre = nombre;
        this.puntosNecesarios = puntosNecesarios;
        this.imagenPath = imagenPath;
        this.colaborador = colaborador;
        this.descripcion = descripcion;
    }

    public BeneficioOfrecido(String nombre, String descripcion, TipoRubro rubro, Integer puntosNecesarios, String imagenPath, Juridica colaborador) {
        this.nombre = nombre;
        this.rubro = rubro;
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
        this.imagenPath = imagenPath;
        this.colaborador = colaborador;
    }
}
