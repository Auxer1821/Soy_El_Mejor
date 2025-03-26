package controllers.controladores.admin;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.donaciones.BeneficioDTO;
import controllers.DTOs.heladera.IncidenteDTO;
import controllers.DTOs.otros.ReporteDTO;
import domain.heladera.incidente.Incidente;
import domain.reportes.ReporteGeneral;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import services.UtilsService;
import services.incidentes.IncidentesService;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController {
    private UtilsService utilsService;
    private IncidentesService incidentesService;

    public AdminController(UtilsService utilsService, IncidentesService incidentesService) {
        this.utilsService = utilsService;
        this.incidentesService = incidentesService;
    }

    public void carga_csv(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/admin/carga_csv.hbs", model);
    }

    public void alta_csv(Context context) {
        UploadedFile uploadedFile = context.uploadedFile("file");

        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        if (uploadedFile != null) {
            try {
                utilsService.realizarMigracion(uploadedFile);

                model.put("exito", "Se ha subido el archivo exitosamente.");

                context.render("routes/admin/carga_csv.hbs", model);
            } catch (Exception e) {
                model.put("error", "Hubo un error al guardar un usuario. Por favor, intentelo nuevamente.");

                context.render("routes/admin/carga_csv.hbs", model);
            }
        } else {
            model.put("error", "Por favor, suba un archivo.");

            context.render("routes/admin/carga_csv.hbs", model);
        }
    }

    public void buscar_reportes(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        context.render("routes/admin/reportes_semanales.hbs", model);
    }

    public void generar_reporte(Context context) throws FileNotFoundException {
        utilsService.generar_reporte();

        context.redirect("/reportes-semanales");
    }

    public void mostrar_reportes(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        LocalDate fecha_inicio = LocalDate.parse(context.formParam("fecha_inicio"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate fecha_fin = LocalDate.parse(context.formParam("fecha_fin"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<ReporteDTO> reportes = utilsService.buscar_reportes_entre(fecha_inicio, fecha_fin);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        if (reportes.isEmpty()) {
            model.put("error", "No existen reportes entre dichas fechas.");
        } else {
            model.put("reporteHeladera", reportes);
        }

        context.render("routes/admin/reportes_semanales.hbs", model);
    }

    public void mostrar_incidentes(Context context) {
        List<IncidenteDTO> incidentes = incidentesService.transformarAIncidenteDTO(utilsService.obtenerTodosLosIncidentes());

        incidentes.stream()
                .map(incidente -> "Incidente: " + incidente.getDescripcion())
                .forEach(System.out::println);

        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);

        model.put("incidentes", incidentes);

        context.render("routes/incidentes/incidentes.hbs", model);
    }

    public void simular_temperatura(Context context) {
        try {
            utilsService.simular_temperaruta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simular_tarjeta(Context context) {
        try {
            utilsService.simular_tarjeta();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
