package domain.forms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class CampoCompletado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "formularioCompletado_id", nullable = false)
    private FormularioCompletado formularioCompletado;

    @ManyToOne
    @JoinColumn(name = "campo_id", nullable = false)
    private Campo campo;

    @Embedded
    private Respuesta respuesta;
}
