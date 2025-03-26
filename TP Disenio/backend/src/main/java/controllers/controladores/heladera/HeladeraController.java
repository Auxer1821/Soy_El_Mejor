package controllers.controladores.heladera;

import services.heladera.HeladeraService;
import utils.javalin.ICrudViewsHandler;
import io.javalin.http.Context;
import repositorios.heladera.RepoHeladera;

public class HeladeraController implements ICrudViewsHandler {
    private HeladeraService heladeraService;

    public HeladeraController(HeladeraService heladeraService) {
        this.heladeraService = heladeraService;
    }

    @Override
    public void index(Context context) {

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

    @Override
    public void delete(Context context) {

    }
}
