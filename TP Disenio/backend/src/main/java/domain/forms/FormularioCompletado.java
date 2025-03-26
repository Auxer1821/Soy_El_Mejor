package domain.forms;

import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.tecnico.Tecnico;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "formularios_completados")
public class FormularioCompletado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="formulario_id")
    private Formulario formulario;

    @OneToMany(mappedBy = "formularioCompletado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CampoCompletado> camposCompletados;

    @OneToOne
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Tecnico tecnico;

    @OneToOne
    @JoinColumn(name = "humana_id", nullable = false)
    private Humana humana;

    @OneToOne
    @JoinColumn(name = "juridica_id", nullable = false)
    private Juridica juridica;
}
