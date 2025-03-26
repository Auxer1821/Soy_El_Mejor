package domain.identificador;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Identificador {
    private String id;

    public Identificador(String id) {
        this.id = id;
    }
}
