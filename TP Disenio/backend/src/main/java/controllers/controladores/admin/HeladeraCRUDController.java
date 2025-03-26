package controllers.controladores.admin;

import controllers.DTOs.heladera.HeladeraDTO;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.ubicaciones.Coordenadas;
import domain.ubicaciones.Direccion;
import domain.ubicaciones.Localidad;
import io.javalin.http.Context;
import services.heladera.HeladeraService;
import services.mapView.MapViewService;
import utils.javalin.ICrudViewsHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeladeraCRUDController implements ICrudViewsHandler {
    private MapViewService mapViewService;
    private HeladeraService heladeraService;

    public HeladeraCRUDController(MapViewService mapViewService, HeladeraService heladeraService) {
        this.mapViewService = mapViewService;
        this.heladeraService = heladeraService;
    }


    @Override
    public void index(Context context) {
        try {
            List<HeladeraDTO> heladeras = mapViewService.obtener_heladeras();

            Map<String, Object> model = new HashMap<>();
            model.put("heladeras", heladeras);

            context.render("routes/admin/heladeraCRUD.hbs", model);
        }
        catch(Exception e){
            e.printStackTrace();
            Map<String, Object> model = new HashMap<>();
            model.put("url", "/mapa-heladeras");
            context.render("/routes/errors/404.hbs", model);
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
        try {
            String nombre = context.formParam("nombreHeladera");
            String direccion = context.formParam("direccion");
            String localidad = context.formParam("localidad");
            String provincia = context.formParam("provincia");
            double latitud = Double.parseDouble(context.formParam("latitud"));
            double longitud = Double.parseDouble(context.formParam("longitud"));
            String estado = context.formParam("estadoHeladera");
            String heladeraId = context.formParam("heladeraId");

            Direccion nuevaDireccion = new Direccion(direccion, new Localidad(localidad, provincia), "Buenos Aires", new Coordenadas(latitud, longitud));
            Heladera datosHeladera = new Heladera();
            datosHeladera.setNombre(nombre);
            datosHeladera.setDireccion(nuevaDireccion);
            datosHeladera.setId(heladeraId);
            datosHeladera.setEstado(Estado.valueOf(estado));

            heladeraService.editarHeladeraPorId(datosHeladera);

            List<HeladeraDTO> heladeras = mapViewService.obtener_heladeras();

            Map<String, Object> model = new HashMap<>();
            model.put("heladeras", heladeras);

            context.render("routes/admin/heladeraCRUD.hbs", model);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("url", "/mapa-heladeras");
            context.render("/routes/errors/404.hbs", model);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {
        try {
            String heladera_id = context.formParam("heladeraId");
            heladeraService.eliminarHeladeraConID(heladera_id);

            List<HeladeraDTO> heladeras = mapViewService.obtener_heladeras();

            Map<String, Object> model = new HashMap<>();
            model.put("heladeras", heladeras);

            context.render("/routes/admin/heladeraCRUD.hbs", model);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
