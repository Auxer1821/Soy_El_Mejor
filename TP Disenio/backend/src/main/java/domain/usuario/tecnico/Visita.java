package domain.usuario.tecnico;

import domain.heladera.incidente.EstadoIncidente;
import domain.heladera.incidente.Incidente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "visitas")
@NoArgsConstructor
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne
    @JoinColumn(name = "incidente_id", referencedColumnName = "id")
    private Incidente incidenteID;

    @ManyToOne
    @JoinColumn(name = "tecnico_cuil", referencedColumnName = "cuil")
    private Tecnico tecnico;

    @Column(name = "foto")
    private String foto;

    @Column(name = "descripcion")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;

    @Column(name = "fecha")
    private LocalDateTime fecha;

}
