package domain.heladera.incidente;

import domain.heladera.Heladera;
import domain.usuario.colaborador.Juridica;
import domain.usuario.colaborador.Humana;
import domain.usuario.tecnico.Tecnico;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name="incidentes")
@NoArgsConstructor
public class Incidente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoIncidente tipoIncidente;

    @ManyToOne
   @JoinColumn(name = "heladera_id", nullable = false, referencedColumnName = "id")
    private Heladera heladera;

    @OneToOne
    @JoinColumn(name = "humana_id")
    private Humana humana;

    @Column(name = "url_foto")
    private String urlFoto;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_incidente")
    private LocalDateTime fecha_incidente;

    @ManyToOne
    @JoinColumn(name = "tecn_asignado", referencedColumnName = "cuil")
    private Tecnico tecnico_asignado;

    @Column(name = "fecha_realizacion")
    private LocalDateTime fecha_realizacion;

    @Enumerated(EnumType.STRING)
    private EstadoIncidente estado;

    public Incidente(TipoIncidente tipoIncidente, Heladera heladera, Humana humana, String urlFoto, String descripcion, LocalDateTime fecha_incidente, Tecnico tecnico_asignado) {
        this.tipoIncidente = tipoIncidente;
        this.heladera = heladera;
        this.humana = humana;
        this.urlFoto = urlFoto;
        this.descripcion = descripcion;
        this.fecha_incidente = fecha_incidente;
        this.tecnico_asignado = tecnico_asignado;
    }

    public Incidente(TipoIncidente tipoIncidente, Heladera heladera, Humana humana, String urlFoto, String descripcion) {
        this.tipoIncidente = tipoIncidente;
        this.heladera = heladera;
        this.humana = humana;
        this.urlFoto = urlFoto;
        this.descripcion = descripcion;
        this.fecha_incidente = LocalDateTime.now();
    }

    public Incidente(TipoIncidente tipoIncidente, Heladera heladera, LocalDateTime fecha) {
        this.tipoIncidente = tipoIncidente;
        this.heladera = heladera;
        this.fecha_incidente = fecha;
    }
}
