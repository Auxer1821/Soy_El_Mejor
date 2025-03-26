package controllers.controladores.colaboraciones.donacionViandas;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.otros.ComunidadDTO;
import io.javalin.http.Context;
import org.eclipse.paho.client.mqttv3.MqttException;
import services.RecomComunidadService;
import services.donaciones.ColaboracionesService;
import utils.javalin.ICrudViewsHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecomComunidadController implements ICrudViewsHandler {
    private RecomComunidadService recomComunidadService;
    private ColaboracionesService colaboracionesService;

    public RecomComunidadController(RecomComunidadService recomComunidadService, ColaboracionesService colaboracionesService) {
        this.recomComunidadService = recomComunidadService;
        this.colaboracionesService = colaboracionesService;
    }

    @Override
    public void index(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/donacion_vianda/recomendador.hbs", model);
    }

    @Override
    public void show(Context context) {
        try {
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            String latParam = context.formParam("lat");
            String lonParam = context.formParam("lon");
            String radioParam = context.formParam("radio");

            if (latParam == null || lonParam == null || radioParam == null) {
                context.status(400).result("Faltan parámetros requeridos.");
                return;
            }

            Double latitud = Double.valueOf(latParam);
            Double longitud = Double.valueOf(lonParam);
            Integer radio = Integer.valueOf(radioParam);

            List<ComunidadDTO> comunidades = recomComunidadService.solicitarComunidades(latitud, longitud, radio);

            Map<String, Object> model = new HashMap<>();
            model.put("comunidades_donantes", comunidades);
            model.put("user", usuarioDTO);
            model.put("titulo", "Listado de comunidades cercanas a la posición enviada");

            // Renderizar la vista
            context.render("routes/colaboraciones/forms_donaciones/donacion_vianda/recomendador.hbs", model);
        } catch (NumberFormatException e) {
            context.status(400).result("Parámetros no válidos.");
            // Aquí también podrías registrar el error si es necesario
        } catch (Exception e) {
            context.status(500).result("Error interno del servidor.");
            // Log del error
            e.printStackTrace();
        }
    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        try{
            colaboracionesService.crearDonacionViandaComunidad(context);

            context.redirect("/inicio");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("datos_usuario", usuarioDTO);
            model.put("error", e.getMessage());

            context.render("routes/colaboraciones/forms_donaciones/donacion_vianda/recomendador.hbs", model);
        }
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
