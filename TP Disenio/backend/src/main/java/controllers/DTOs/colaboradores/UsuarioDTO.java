package controllers.DTOs.colaboradores;

import domain.usuario.colaborador.UsuarioPersistente;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioDTO {
    private String id;
    private String username;
    private String rol;
    private String puntosAdquiridos;
    private String email;

    public UsuarioDTO(String id, String username, String rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public UsuarioDTO(String id, String username, String rol, String email) {
        this.id = id;
        this.username = username;
        this.rol = rol;
        this.email = email;
    }

    public static UsuarioDTO obtenerInfoUsuario(Context context){
        return new UsuarioDTO(
                context.sessionAttribute("id_usuario"),
                context.sessionAttribute("username"),
                context.sessionAttribute("rol"),
                context.sessionAttribute("email")
        );
    }
    public void actualizar (UsuarioPersistente usuarioPersistente){
        this.id = usuarioPersistente.getId();
        this.username = usuarioPersistente.getUsername();
        this.rol = String.valueOf(usuarioPersistente.getRolUsuario());
        this.email = usuarioPersistente.getEmail();
        this.puntosAdquiridos = String.valueOf(usuarioPersistente.getPuntos_adquiridos());
    }
}


