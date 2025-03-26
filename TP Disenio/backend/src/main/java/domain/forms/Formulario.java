package domain.forms;

import domain.identificador.Identificador;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "formularios")
public class Formulario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "formulario_campo",
            joinColumns = @JoinColumn(name = "formulario_id",
                    referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "campo_id", referencedColumnName = "id")
    )
    private List<Campo> campos;
}
