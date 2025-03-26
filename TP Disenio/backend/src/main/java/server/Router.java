package server;

import config.ServiceLocator;
import controllers.controladores.admin.AdminController;
import controllers.controladores.admin.HeladeraCRUDController;
import controllers.controladores.admin.TecnicosCRUDController;
import controllers.controladores.colaboraciones.ColaboracionesController;
import controllers.controladores.colaboraciones.donacionViandas.DonacionViandasController;
import controllers.controladores.mapView.MapViewController;
import controllers.controladores.colaboraciones.donacionViandas.RecomComunidadController;
import controllers.controladores.colaboraciones.BeneficiosController;
import controllers.controladores.puntos.PuntosController;
import controllers.controladores.usuarios.ColaboradorController;
import controllers.controladores.HomePageController;

import domain.usuario.colaborador.RolUsuario;
import io.javalin.Javalin;

public class Router {

    public static void init(Javalin app) {
        //PROYECTO
        app.get("/", ServiceLocator.instanceOf(HomePageController.class)::index);

        app.get("/inicio", ServiceLocator.instanceOf(HomePageController.class)::show, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        app.get("/login", ServiceLocator.instanceOf(ColaboradorController.class)::login);

        app.post("/login", ServiceLocator.instanceOf(ColaboradorController.class)::check_login);

        app.get("/cerrar-sesion", ServiceLocator.instanceOf(HomePageController.class)::cerrarSesion);

        app.get("/registrarse", ServiceLocator.instanceOf(ColaboradorController.class)::register);

        app.get("/registrarse/humana", ServiceLocator.instanceOf(ColaboradorController.class)::crear_humana);

        app.get("/registrarse/humana/{id}", ServiceLocator.instanceOf(ColaboradorController.class)::crear_humana_precargada);

        app.post("/registrarse/humana", ServiceLocator.instanceOf(ColaboradorController.class)::guardar_humana);

        app.post("/registrarse/humana/{id}", ServiceLocator.instanceOf(ColaboradorController.class)::guardar_humano_precargado);

        app.get("/registrarse/juridica", ServiceLocator.instanceOf(ColaboradorController.class)::crear_juridica);

        app.post("/registrarse/juridica", ServiceLocator.instanceOf(ColaboradorController.class)::guardar_juridica);

        // Beneficios
        app.get("/beneficios", ServiceLocator.instanceOf(BeneficiosController.class)::index, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        app.post("/beneficios", ServiceLocator.instanceOf(BeneficiosController.class)::canjearBeneficio, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        app.get("/historia", ServiceLocator.instanceOf(HomePageController.class)::aboutUs);

        //Puntos
        app.get("/mis-puntos", ServiceLocator.instanceOf(PuntosController.class)::misPuntos);

        // Colaboraciones
        app.get("/colaboraciones", ServiceLocator.instanceOf(ColaboracionesController.class)::index, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        // Donacion Vianda
        app.get("/colaboraciones/donar-vianda", ServiceLocator.instanceOf(DonacionViandasController.class)::select_donde_donar_vianda, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.get("/colaboraciones/donar-a-una-heladera", ServiceLocator.instanceOf(DonacionViandasController.class)::buscar_heladera_donde_donar, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/donar-a-una-heladera", ServiceLocator.instanceOf(DonacionViandasController.class)::guardar_donacion_vianda, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.get("/colaboraciones/donar-a-comunidades", ServiceLocator.instanceOf(RecomComunidadController.class)::index, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/donar-a-comunidades", ServiceLocator.instanceOf(RecomComunidadController.class)::show, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/donar-a-comunidades/vianda", ServiceLocator.instanceOf(RecomComunidadController.class)::save, RolUsuario.ADMIN, RolUsuario.HUMANA);

        // Donacion dinero
        app.get("/colaboraciones/donar-dinero", ServiceLocator.instanceOf(ColaboracionesController.class)::crear_donacion_dinero, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        app.post("/colaboraciones/donar-dinero", ServiceLocator.instanceOf(ColaboracionesController.class)::guardar_donacion_dinero, RolUsuario.ADMIN, RolUsuario.HUMANA, RolUsuario.JURIDICA);

        // Distribucion Viandas
        app.get("/colaboraciones/distribuir-viandas", ServiceLocator.instanceOf(ColaboracionesController.class)::crear_distribucion_viandas, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/distribuir-viandas", ServiceLocator.instanceOf(ColaboracionesController.class)::buscar_distribucion_viandas, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/distribuir-viandas/{id}", ServiceLocator.instanceOf(ColaboracionesController.class)::asignar_distribucion_viandas, RolUsuario.ADMIN, RolUsuario.HUMANA);

        // Registro de vulnerable
        app.get("/colaboraciones/registrar-vulnerable", ServiceLocator.instanceOf(ColaboracionesController.class)::registrar_vulnerable, RolUsuario.ADMIN, RolUsuario.HUMANA);

        app.post("/colaboraciones/registrar-vulnerable", ServiceLocator.instanceOf(ColaboracionesController.class)::guardar_vulnerable, RolUsuario.ADMIN, RolUsuario.HUMANA);

        // Ofrecer beneficio
        app.get("/colaboraciones/ofrecer-beneficio", ServiceLocator.instanceOf(ColaboracionesController.class)::crear_beneficio, RolUsuario.ADMIN, RolUsuario.JURIDICA);

        app.post("/colaboraciones/ofrecer-beneficio", ServiceLocator.instanceOf(ColaboracionesController.class)::guardar_beneficio, RolUsuario.ADMIN, RolUsuario.JURIDICA);

        // Encargarse de Heladera
        app.get("/colaboraciones/encargarse-de-heladera", ServiceLocator.instanceOf(ColaboracionesController.class)::crear_encargo_heladera, RolUsuario.ADMIN, RolUsuario.JURIDICA);

        app.post("/colaboraciones/encargarse-de-heladera", ServiceLocator.instanceOf(ColaboracionesController.class)::guardar_encargo_heladera, RolUsuario.ADMIN, RolUsuario.JURIDICA);

        // MapView
        app.get("/mapa-heladeras", ServiceLocator.instanceOf(MapViewController.class)::index, RolUsuario.ADMIN, RolUsuario.JURIDICA, RolUsuario.HUMANA);

        app.post("/mapa-heladeras/suscripcion", ServiceLocator.instanceOf(MapViewController.class)::suscribirseHeladera, RolUsuario.ADMIN, RolUsuario.HUMANA);

        // Reportar Falla
        app.post("/mapa-heladeras/reportar-falla", ServiceLocator.instanceOf(MapViewController.class)::reportarFalla, RolUsuario.ADMIN, RolUsuario.HUMANA);

        // Reportes
        app.get("/reportes-semanales", ServiceLocator.instanceOf(AdminController.class)::buscar_reportes, RolUsuario.JURIDICA, RolUsuario.ADMIN);

        app.post("/reportes-semanales", ServiceLocator.instanceOf(AdminController.class)::mostrar_reportes, RolUsuario.JURIDICA, RolUsuario.ADMIN);

        app.post("/generar-reporte", ServiceLocator.instanceOf(AdminController.class)::generar_reporte, RolUsuario.JURIDICA, RolUsuario.ADMIN);

        // Incidentes
        app.get("/incidentes-heladeras", ServiceLocator.instanceOf(AdminController.class)::mostrar_incidentes, RolUsuario.ADMIN, RolUsuario.JURIDICA);

        // ADMIN
        app.get("/carga-csv", ServiceLocator.instanceOf(AdminController.class)::carga_csv, RolUsuario.ADMIN);

        app.post("/alta-csv", ServiceLocator.instanceOf(AdminController.class)::alta_csv, RolUsuario.ADMIN);

        app.get("/heladeras", ServiceLocator.instanceOf(HeladeraCRUDController.class)::index, RolUsuario.ADMIN);

        app.post("/eliminar-heladera", ServiceLocator.instanceOf(HeladeraCRUDController.class)::delete, RolUsuario.ADMIN);

        app.post("/editar-heladera", ServiceLocator.instanceOf(HeladeraCRUDController.class)::edit, RolUsuario.ADMIN);

        app.get("/tecnicos", ServiceLocator.instanceOf(TecnicosCRUDController.class)::index, RolUsuario.ADMIN);

        app.post("/tecnico-deshabilitado", ServiceLocator.instanceOf(TecnicosCRUDController.class)::deshabilitar, RolUsuario.ADMIN);

        app.post("/tecnico-habilitado", ServiceLocator.instanceOf(TecnicosCRUDController.class)::habilitar, RolUsuario.ADMIN);

        app.get("/simulador-tarjeta", ServiceLocator.instanceOf(AdminController.class)::simular_tarjeta, RolUsuario.ADMIN);
        app.get("/simulador-temperatura", ServiceLocator.instanceOf(AdminController.class)::simular_temperatura, RolUsuario.ADMIN);
    }
}
