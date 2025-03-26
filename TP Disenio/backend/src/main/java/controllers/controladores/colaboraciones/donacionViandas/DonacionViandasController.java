package controllers.controladores.colaboraciones.donacionViandas;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.heladera.HeladeraDTO;
import domain.heladera.Heladera;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.paho.client.mqttv3.MqttException;
import services.donaciones.ColaboracionesService;
import services.mapView.MapViewService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DonacionViandasController {
    private ColaboracionesService colaboracionesService;
    private MapViewService mapViewService;

    public DonacionViandasController(ColaboracionesService colaboracionesService, MapViewService mapViewService) {
        this.colaboracionesService = colaboracionesService;
        this.mapViewService = mapViewService;
    }

    // Donacion vianda
    public void select_donde_donar_vianda(Context context){
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/donacion_vianda/donar_vianda.hbs", model);
    }

    public void buscar_heladera_donde_donar(Context context){
        try {
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);
            List<HeladeraDTO> heladerasDisponibles = mapViewService.obtener_heladeras_disponibles();

            Map<String, Object> model = new HashMap<>();
            model.put("heladeras", heladerasDisponibles);
            model.put("user", usuarioDTO);

            context.render("routes/colaboraciones/forms_donaciones/donacion_vianda/donar_a_heladera.hbs", model);
        }
        catch (Exception e){
            context.status(500).result("Error interno del servidor.");
            context.redirect("/colaboraciones");
        }
    }

    public void guardar_donacion_vianda(Context context) throws MqttException, InterruptedException {
        try{
            colaboracionesService.crearDonacionViandaHeladera(context);

            context.redirect("/inicio");
        }catch (Exception e){
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", "Ocurrió un error inesperado. No se pudo completar la transaccion.");

            context.status(HttpStatus.SERVICE_UNAVAILABLE);
            context.redirect("/colaboraciones/donar-a-una-heladera");
        }

    }


}
