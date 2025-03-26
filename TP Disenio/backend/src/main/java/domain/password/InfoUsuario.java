package domain.password;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter

public class InfoUsuario {
    private ArrayList<String> contexto;
    private String usuario;
    private String contrasenia;

    public InfoUsuario(ArrayList<String> contexto, String usuario, String contrasenia) {
        this.contexto = contexto;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
    }
}

