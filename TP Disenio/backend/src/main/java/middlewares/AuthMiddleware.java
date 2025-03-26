package middlewares;

import domain.usuario.colaborador.RolUsuario;
import exceptions.AccessDeniedException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AuthMiddleware {

    public static void apply(Javalin app) {
        app.beforeMatched(ctx -> {
            RolUsuario rolUsuario = getUserRoleType(ctx);
            if (!ctx.routeRoles().isEmpty() && !ctx.routeRoles().contains(rolUsuario)) {
                throw new AccessDeniedException();
            }
        });
    }

    private static RolUsuario getUserRoleType(Context context) {
        return context.sessionAttribute("rol") != null?
                RolUsuario.valueOf(context.sessionAttribute("rol")) : null;
    }
}
