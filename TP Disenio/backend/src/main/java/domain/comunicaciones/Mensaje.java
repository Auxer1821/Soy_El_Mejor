package domain.comunicaciones;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Mensaje {
    private String mensaje;
    private String subject;

    public Mensaje (String cuerpoMensaje, String subject) {
        this.mensaje = cuerpoMensaje;
        this.subject = subject;
    }
}
