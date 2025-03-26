package controllers.controladores;

import controllers.DTOs.colaboradores.UsuarioDTO;
import io.javalin.http.Context;
import utils.javalin.ICrudViewsHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HomePageController implements ICrudViewsHandler {

    @Override
    public void index(Context context) {
        if(context.sessionAttribute("rol") != null){
            context.redirect("/inicio");
        }else {
            context.render("/routes/homepage/homepage.hbs");
        }
    }

    @Override
    public void show(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("/routes/homepage/homepage.hbs", model);
    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {

    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {

    }

    public void cerrarSesion(Context context){
        context.cookieStore().clear();

        context.req().getSession().removeAttribute("rol");
        context.req().getSession().removeAttribute("username");


        context.redirect("/");
    }

    public void aboutUs(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("/routes/homepage/aboutUs/aboutUs.hbs", model);
    }

}
