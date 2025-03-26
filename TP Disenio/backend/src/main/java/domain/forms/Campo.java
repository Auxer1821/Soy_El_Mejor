package domain.forms;

import domain.identificador.Identificador;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "campos")
public class Campo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_campo", nullable = false)
    private String nombreCampo;

    @Column(name = "obligatorio", nullable = false)
    private Boolean esObligatorio;

    @Enumerated(EnumType.STRING)
    @Column(name = "formato", nullable = false)
    private FormatoCampo formato;

    @ElementCollection
    private List<String> opciones;
}
