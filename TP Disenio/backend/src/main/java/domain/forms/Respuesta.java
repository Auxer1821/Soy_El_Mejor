package domain.forms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.util.List;

@Getter
@Setter
@Embeddable
public class Respuesta {
    @ElementCollection
    private List<String> opcionesElegidas;
    private String respuestaLibre;
}
