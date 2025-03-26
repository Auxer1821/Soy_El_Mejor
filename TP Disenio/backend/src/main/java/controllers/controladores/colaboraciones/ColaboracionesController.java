package controllers.controladores.colaboraciones;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.donaciones.BeneficioDTO;
import domain.colaboraciones.DistribucionViandas;
import domain.heladera.Modelo;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UploadedFile;
import services.donaciones.ColaboracionesService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ColaboracionesController {
    private ColaboracionesService colaboracionesService;

    public ColaboracionesController(ColaboracionesService colaboracionesService) {
        this.colaboracionesService = colaboracionesService;
    }

    public void index(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/colaboraciones.hbs", model);
    }

    // Donacion dinero
    public void crear_donacion_dinero(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/donar_dinero.hbs", model);
    }

    public void guardar_donacion_dinero(Context context) {
        try {
            colaboracionesService.crearDonacionDinero(context);
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("status", true);
            context.render("routes/colaboraciones/forms_donaciones/donar_dinero.hbs", model);
        } catch (Exception e) {
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", "Ocurrió un error inesperado. No se pudo completar la transaccion.");

            context.status(HttpStatus.SERVICE_UNAVAILABLE);
            context.render("routes/colaboraciones/forms_donaciones/donar_dinero.hbs", model);
        }
    }

    // Distribucion de viandas
    public void crear_distribucion_viandas(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/distribuir_viandas.hbs", model);
    }

    public void buscar_distribucion_viandas(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);
        try {
            DistribucionViandas distribucion = colaboracionesService.buscarDistribucion(context.formParam("cod_distribucion"));

            model.put("distribucion", distribucion);
            context.render("routes/colaboraciones/forms_donaciones/distribuir_viandas.hbs", model);
        } catch (Exception e) {
            model.put("error", e.getMessage());

            context.render("routes/colaboraciones/forms_donaciones/distribuir_viandas.hbs", model);
        }
    }

    public void asignar_distribucion_viandas(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        try {
            colaboracionesService.asignarDistribucion(context.formParam("cod_distribucion"), usuarioDTO.getId());

            context.redirect("/inicio");
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", e.getMessage());

            context.render("routes/colaboraciones/forms_donaciones/distribuir_viandas.hbs", model);
        }
    }

    // Ofrecer beneficio
    public void crear_beneficio(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/ofrecer_beneficio.hbs", model);
    }

    public void guardar_beneficio(Context context) {
        BeneficioDTO beneficioDTO = BeneficioDTO.obtenerBeneficioDTO(context);
        UploadedFile uploadedFile = context.uploadedFile("file");

        try {
            if (uploadedFile != null) {
                String directory = "backend/src/main/resources/public/img/beneficios";
                Path destinationDirectory = Paths.get(directory);

                if (!Files.exists(destinationDirectory)) {
                    Files.createDirectories(destinationDirectory);
                }

                String sanitizedFilename = uploadedFile.filename().replaceAll("\\s+", "_");
                Path destination = destinationDirectory.resolve(sanitizedFilename);

                if (!uploadedFile.contentType().startsWith("image/")) {
                    context.result("El archivo subido no es una imagen.");
                    return;
                }

                InputStream inputStream = uploadedFile.content();
                if (Files.exists(destination)) {
                    String newFilename = System.currentTimeMillis() + "_" + sanitizedFilename;
                    destination = destinationDirectory.resolve(newFilename);


                    Files.copy(inputStream, destination);
                    beneficioDTO.setImagenPath("/img/beneficios/" + newFilename);
                } else {
                    Files.copy(inputStream, destination);
                    beneficioDTO.setImagenPath("/img/beneficios/" + uploadedFile.filename());
                }

                colaboracionesService.crearBeneficio(beneficioDTO, context.sessionAttribute("id_usuario"));

                context.redirect("/colaboraciones");
            }
        } catch (IOException e) {
            e.printStackTrace();
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", e.getMessage());
            model.put("beneficio", beneficioDTO);

            context.render("routes/colaboraciones/forms_donaciones/ofrecer_beneficio.hbs", model);
        }
    }

    public void modificar_beneficio(Context context) {
        //TODO
    }

    public void eliminar_beneficio(Context context) {
        //TODO
    }

    // Encargarse de heladera
    public void crear_encargo_heladera(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        List<Modelo> modelosHeladera = colaboracionesService.obtenerModelosHeladera();

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);
        model.put("modelo", modelosHeladera);

        context.render("routes/colaboraciones/forms_donaciones/encargo_heladera.hbs", model);
    }

    public void guardar_encargo_heladera(Context context) {
        try {
            colaboracionesService.crearEncargoHeladera(context);
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("status", true);
            context.render("routes/colaboraciones/forms_donaciones/encargo_heladera.hbs", model);

        } catch (Exception e) {
            List<Modelo> modelosHeladera = colaboracionesService.obtenerModelosHeladera();

            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", e.getMessage());
            model.put("modelo", modelosHeladera);

            context.status(HttpStatus.SERVICE_UNAVAILABLE);
            context.render("routes/colaboraciones/forms_donaciones/encargo_heladera.hbs", model);
        }
    }

    public void eliminar_encargo_heladera(Context context) {
        //TODO
    }

    // Registrar vulnerable
    public void registrar_vulnerable(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/colaboraciones/forms_donaciones/registro_vulnerable.hbs", model);
    }

    public void guardar_vulnerable(Context context) {

        try {
            colaboracionesService.registrarPersonaVulnerable(context);
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("status", true);
            context.render("routes/colaboraciones/forms_donaciones/registro_vulnerable.hbs", model);
        } catch (Exception e) {
            UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

            Map<String, Object> model = new HashMap<>();
            model.put("user", usuarioDTO);
            model.put("error", "Ocurrió un error inesperado. No se pudo completar la transaccion.");

            context.status(HttpStatus.SERVICE_UNAVAILABLE);
            context.render("routes/colaboraciones/forms_donaciones/registro_vulnerable.hbs", model);
        }

    }

}
