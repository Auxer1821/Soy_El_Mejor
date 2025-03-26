package controllers.controladores.admin;

import controllers.DTOs.tecnicos.TecnicoDTO;
import io.javalin.http.Context;
import repositorios.tecnico.RepoTecnicos;
import services.tecnicos.TecnicosService;
import utils.javalin.ICrudViewsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TecnicosCRUDController implements ICrudViewsHandler {
    private TecnicosService tecnicosService;

    public TecnicosCRUDController(TecnicosService tecnicosService) {
        this.tecnicosService = tecnicosService;
    }

    @Override
    public void index(Context context) {
        try {
            List<TecnicoDTO> tecnicosDTOS = tecnicosService.obtenerTodos();

            Map<String, Object> model = new HashMap<>();
            model.put("tecnicos", tecnicosDTOS);
            context.render("routes/admin/tecnicosCRUD.hbs", model);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }

    }

    @Override
    public void delete(Context context) {
    }

    ;

    public void deshabilitar(Context context) {
        try {
            String cuil = context.formParam("cuil");

            if (!(cuil == null)) {
                System.out.println(cuil);
                tecnicosService.modificarHabilitacionPorCuil(cuil, false);

                List<TecnicoDTO> tecnicoDTOS = tecnicosService.obtenerTodos();
                Map<String, Object> model = new HashMap<>();
                model.put("tecnicos", tecnicoDTOS);

                context.render("routes/admin/tecnicosCRUD.hbs", model);
            }
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public void habilitar(Context context) {
        try {
            String cuil = context.formParam("cuil");

            if (!(cuil == null)) {
                System.out.println(cuil);
                tecnicosService.modificarHabilitacionPorCuil(cuil, true);

                List<TecnicoDTO> tecnicoDTOS = tecnicosService.obtenerTodos();
                Map<String, Object> model = new HashMap<>();
                model.put("tecnicos", tecnicoDTOS);

                context.render("routes/admin/tecnicosCRUD.hbs", model);
            }
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public void show(Context context) {

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


}
