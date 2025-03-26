package controllers.controladores.mapView;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.heladera.HeladeraDTO;
import controllers.DTOs.heladera.ReporteFallaDTO;
import controllers.DTOs.suscripciones.SuscripcionDTO;
import io.javalin.http.Context;
import services.ReportarFallaService;
import services.mapView.MapViewService;
import services.suscripciones.SuscripcionHeladeraService;
import utils.javalin.ICrudViewsHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapViewController implements ICrudViewsHandler {
    private SuscripcionHeladeraService suscripcionHeladeraService;
    private ReportarFallaService reportarFallaService;
    private MapViewService mapViewService;

    public MapViewController(SuscripcionHeladeraService suscripcionHeladeraService, ReportarFallaService reportarFallaService, MapViewService mapViewService) {
        this.suscripcionHeladeraService = suscripcionHeladeraService;
        this.mapViewService = mapViewService;
        this.reportarFallaService = reportarFallaService;
    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void index(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        try {
            List<HeladeraDTO> heladeras = mapViewService.obtener_heladeras();

            Map<String, Object> model = new HashMap<>();
            model.put("heladeras", heladeras);
            model.put("user", usuarioDTO);

            context.render("routes/mapView/map.hbs", model);
        }
        catch(Exception e){
            Map<String, Object> model = new HashMap<>();
            model.put("url", "/mapa-heladeras");
            model.put("user", usuarioDTO);
            context.render("/routes/errors/404.hbs", model);
        }
    }

    @Override
    public void show(Context context) {

    }

    public void suscribirseHeladera(Context context) {
        try {
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);
            String heladeraId = context.formParam("heladeraId");
            List<String> suscripciones = context.formParams("suscribirseA");
            String cantidadViandas = context.formParam("cantidadViandas");

            SuscripcionDTO suscripcionDTO = new SuscripcionDTO(
                    usuarioDTO.getId(),
                    heladeraId,
                    suscripciones,
                    cantidadViandas);

            suscripcionHeladeraService.realizar_suscripcion(suscripcionDTO);
        }
        catch(Exception e) {
            context.status(500).result("Error interno del servidor.");
            context.redirect("/mapa-heladeras");
        }
    }

    public void reportarFalla(Context context) {
        try {
            ReporteFallaDTO reporteFallaTO = new ReporteFallaDTO(
                    context.formParam("heladeraId"),
                    context.formParam("fecha"),
                    context.sessionAttribute("id_usuario"),
                    context.formParam("descripcion"),
                    context.formParam("imagenes")
            );

            reportarFallaService.registrarIncidente(reporteFallaTO);
        } catch (Exception e) {
            context.status(500).result("Error interno del servidor.");
            context.redirect("/mapa-heladeras");
        }
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
