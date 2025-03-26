package domain.usuario.colaborador;

import io.javalin.security.RouteRole;

public enum RolUsuario implements RouteRole {
    ADMIN,
    HUMANA,
    JURIDICA,
}
