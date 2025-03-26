package server.handlers;

import controllers.DTOs.colaboradores.UsuarioDTO;
import exceptions.AccessDeniedException;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

public class AccessDeniedHandler implements IHandler {

    @Override
    public void setHandle(Javalin app) {
        app.exception(AccessDeniedException.class, (e, context) -> {
            UsuarioDTO usuarioDTO = new UsuarioDTO(
                    context.sessionAttribute("id_usuario"),
                    context.sessionAttribute("username"),
                    context.sessionAttribute("rol")
            );

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);

            context.status(401);
            context.render("/routes/errors/401.hbs", model);
        });
    }
}
