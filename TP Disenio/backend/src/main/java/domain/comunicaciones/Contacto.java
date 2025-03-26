package domain.comunicaciones;

import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.tecnico.Tecnico;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "contactos")
@NoArgsConstructor
public class Contacto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoDeContacto tipo;

    @Column(name="dato_contacto", nullable = false)
    private String datoContacto;

    @ManyToOne
    @JoinColumn(name = "id_humana", referencedColumnName = "id")
    private Humana humana;

    @ManyToOne
    @JoinColumn(name = "id_juridica", referencedColumnName = "id")
    private Juridica juridica;

    @OneToOne
    @JoinColumn(name = "id_tecnico", referencedColumnName = "cuil")
    private Tecnico tecnico;

    public Contacto (TipoDeContacto tipo, String datoContacto, Humana humana) {
        this.tipo = tipo;
        this.datoContacto = datoContacto;
        this.humana = humana;
    }

    public Contacto(TipoDeContacto tipo, String datoContacto, Juridica juridica) {
        this.tipo = tipo;
        this.datoContacto = datoContacto;
        this.juridica = juridica;
    }

    public Contacto(TipoDeContacto tipo, String datoContacto, Tecnico tecnico) {
        this.tipo = tipo;
        this.datoContacto = datoContacto;
        this.tecnico = tecnico;
    }
}
