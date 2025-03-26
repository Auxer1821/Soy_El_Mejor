package domain.usuario.tecnico;

import domain.heladera.Heladera;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "informes_tecnicos")
public class InformeTecnico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_visita")
    private LocalDate fechaVisita;

    @ManyToOne
    @JoinColumn(name = "heladera_id")
    private Heladera heladera;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "url_foto")
    private String url_foto;

    @Column(name = "arreglada")
    private Boolean arreglada;

    @OneToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;
}
