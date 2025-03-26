package config;

import controllers.controladores.admin.AdminController;
import controllers.controladores.admin.HeladeraCRUDController;
import controllers.controladores.admin.TecnicosCRUDController;
import controllers.controladores.colaboraciones.BeneficiosController;
import controllers.controladores.colaboraciones.ColaboracionesController;
import controllers.controladores.colaboraciones.donacionViandas.DonacionViandasController;
import controllers.controladores.heladera.HeladeraController;
import controllers.controladores.mapView.MapViewController;
import controllers.controladores.colaboraciones.donacionViandas.RecomComunidadController;
import controllers.controladores.puntos.PuntosController;
import controllers.controladores.usuarios.ColaboradorController;
import controllers.controladores.HomePageController;
import domain.password.ValidadorDeContrasenia;
import repositorios.RepoGenerico;
import repositorios.RepositorioDeBeneficios;
import repositorios.colaboraciones.RepoComunidades;
import repositorios.colaboraciones.RepoColaboraciones;
import repositorios.colaboraciones.RepoRegistroPersonasVulnerables;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.colaboradores.RepoUsuario;
import repositorios.contactos.RepoContactos;
import repositorios.heladera.RepoHeladera;
import repositorios.heladera.RepoMovimientos;
import repositorios.puntos.RepositorioDeCanjes;
import repositorios.tarjetas.RepoSolicitudesApertura;
import repositorios.tarjetas.RepoTarjetas;
import repositorios.tecnico.RepoTecnicos;
import repositorios.visitas.RepoVisitas;
import services.ColaboradorService;
import services.Mqtt.MqttService;
import services.RecomComunidadService;
import services.ReportarFallaService;
import services.UtilsService;
import services.cargaMasiva.MigracionesService;
import services.donaciones.BeneficiosService;
import services.donaciones.ColaboracionesService;
import services.heladera.HeladeraService;
import services.incidentes.IncidentesService;
import services.mapView.MapViewService;
import services.notificadores.NotificadorDeTecnicos;
import services.puntos.PuntosService;
import services.sensores.SensorMovimientoService;
import services.sensores.SensorTemperaturaService;
import services.suscripciones.SuscripcionHeladeraService;
import services.tecnicos.TecnicosService;
import utils.Varios.GestorDeArchivo;
import utils.javalin.seeder.GestorBroker;
import utils.telegramSender.TelegramSender;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static Map<String, Object> instances = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T instanceOf(Class<T> componentClass) {
        String componentName = componentClass.getName();

        if (!instances.containsKey(componentName)) {
            if (componentName.equals(ColaboradorController.class.getName())) {
                ColaboradorController instance = new ColaboradorController(instanceOf(ColaboradorService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboradorService.class.getName())) {
                ColaboradorService instance = new ColaboradorService(instanceOf(RepoUsuario.class),
                        instanceOf(RepoHumana.class), instanceOf(RepoJuridica.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoJuridica.class.getName())) {
                RepoJuridica instance = new RepoJuridica();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoHumana.class.getName())) {
                RepoHumana instance = new RepoHumana();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoUsuario.class.getName())) {
                RepoUsuario instance = new RepoUsuario();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoGenerico.class.getName())) {
                RepoGenerico instance = new RepoGenerico();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoSolicitudesApertura.class.getName())) {
                RepoSolicitudesApertura instance = new RepoSolicitudesApertura();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoHeladera.class.getName())) {
                RepoHeladera instance = new RepoHeladera();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoComunidades.class.getName())) {
                RepoComunidades instance = new RepoComunidades();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoTarjetas.class.getName())) {
                RepoTarjetas instance = new RepoTarjetas();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoColaboraciones.class.getName())) {
                RepoColaboraciones instance = new RepoColaboraciones();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoRegistroPersonasVulnerables.class.getName())) {
                RepoRegistroPersonasVulnerables instance = new RepoRegistroPersonasVulnerables();
                instances.put(componentName, instance);
            } else if (componentName.equals(BeneficiosController.class.getName())) {
                BeneficiosController instance = new BeneficiosController(instanceOf(BeneficiosService.class),
                        instanceOf(ColaboradorService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(BeneficiosService.class.getName())) {
                BeneficiosService instance = new BeneficiosService(instanceOf(RepositorioDeBeneficios.class),
                        instanceOf(RepoGenerico.class), instanceOf(RepositorioDeCanjes.class),
                        instanceOf(RepoJuridica.class), instanceOf(RepoHumana.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepositorioDeBeneficios.class.getName())) {
                RepositorioDeBeneficios instance = new RepositorioDeBeneficios();
                instances.put(componentName, instance);
            } else if (componentName.equals(RecomComunidadController.class.getName())) {
                RecomComunidadController instance = new RecomComunidadController(
                        instanceOf(RecomComunidadService.class), instanceOf(ColaboracionesService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RecomComunidadService.class.getName())) {
                RecomComunidadService instance = new RecomComunidadService();
                instances.put(componentName, instance);
            }

            /* Validador contraseñas */
            else if (componentName.equals(ValidadorDeContrasenia.class.getName())) {
                ValidadorDeContrasenia instance = new ValidadorDeContrasenia();
                instances.put(componentName, instance);
            } else if (componentName.equals(GestorDeArchivo.class.getName())) {
                GestorDeArchivo instance = new GestorDeArchivo();
                instances.put(componentName, instance);
            }

            /* HOMEPAGE CONTROLLER */
            else if (componentName.equals(HomePageController.class.getName())) {
                HomePageController instance = new HomePageController();
                instances.put(componentName, instance);
            }

            /* COLABORACIONES CONTROLLER */
            else if (componentName.equals(ColaboracionesController.class.getName())) {
                ColaboracionesController instance = new ColaboracionesController(
                        instanceOf(ColaboracionesService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboracionesService.class.getName())) {
                ColaboracionesService instance = new ColaboracionesService(instanceOf(RepoGenerico.class),
                        instanceOf(RepoHeladera.class), instanceOf(RepoJuridica.class), instanceOf(RepoHumana.class),
                        instanceOf(RepoComunidades.class), instanceOf(RepoTarjetas.class),
                        instanceOf(RepoColaboraciones.class), instanceOf(RepoRegistroPersonasVulnerables.class),
                        instanceOf(RepoTecnicos.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(DonacionViandasController.class.getName())) {
                DonacionViandasController instance = new DonacionViandasController(
                        instanceOf(ColaboracionesService.class), instanceOf(MapViewService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(MapViewController.class.getName())) {
                MapViewController instance = new MapViewController(instanceOf(SuscripcionHeladeraService.class),
                        instanceOf(ReportarFallaService.class), instanceOf(MapViewService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(MapViewService.class.getName())) {
                MapViewService instance = new MapViewService(instanceOf(RepoHeladera.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(SuscripcionHeladeraService.class.getName())) {
                SuscripcionHeladeraService instance = new SuscripcionHeladeraService(instanceOf(RepoHeladera.class),
                        instanceOf(RepoHumana.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(ReportarFallaService.class.getName())) {
                ReportarFallaService instance = new ReportarFallaService(instanceOf(RepoHeladera.class),
                        instanceOf(RepoHumana.class), instanceOf(RepoJuridica.class), instanceOf(RepoGenerico.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(HeladeraService.class.getName())) {
                HeladeraService instance = new HeladeraService(instanceOf(RepoHeladera.class),
                        instanceOf(RepoGenerico.class), instanceOf(RepoColaboraciones.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(HeladeraController.class.getName())) {
                HeladeraController instance = new HeladeraController(instanceOf(HeladeraService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(SensorMovimientoService.class.getName())) {
                SensorMovimientoService instance = new SensorMovimientoService(instanceOf(RepoGenerico.class),
                        instanceOf(RepoHeladera.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(SensorTemperaturaService.class.getName())) {
                SensorTemperaturaService instance = new SensorTemperaturaService(instanceOf(RepoGenerico.class),
                        instanceOf(RepoHeladera.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(MqttService.class.getName())) {
                MqttService instance = new MqttService(instanceOf(RepoSolicitudesApertura.class),
                        instanceOf(RepoColaboraciones.class), instanceOf(RepoTarjetas.class),
                        instanceOf(RepoHeladera.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(AdminController.class.getName())) {
                AdminController instance = new AdminController(instanceOf(UtilsService.class),
                        instanceOf(IncidentesService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(UtilsService.class.getName())) {
                UtilsService instance = new UtilsService(instanceOf(RepoHeladera.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(HeladeraCRUDController.class.getName())) {
                HeladeraCRUDController instance = new HeladeraCRUDController(instanceOf(MapViewService.class),
                        instanceOf(HeladeraService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(GestorBroker.class.getName())) {
                GestorBroker instance = new GestorBroker();
                instances.put(componentName, instance);
            } else if (componentName.equals(NotificadorDeTecnicos.class.getName())) {
                NotificadorDeTecnicos instance = new NotificadorDeTecnicos(instanceOf(RepoTecnicos.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(PuntosController.class.getName())) {
                PuntosController instance = new PuntosController(instanceOf(PuntosService.class),
                        instanceOf(BeneficiosService.class), instanceOf(ColaboradorService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(PuntosService.class.getName())) {
                PuntosService instance = new PuntosService(instanceOf(RepositorioDeCanjes.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepositorioDeCanjes.class.getName())) {
                RepositorioDeCanjes instance = new RepositorioDeCanjes();
                instances.put(componentName, instance);
            } else if (componentName.equals(MigracionesService.class.getName())) {
                MigracionesService instance = new MigracionesService(instanceOf(RepoHumana.class),
                        instanceOf(RepoGenerico.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoMovimientos.class.getName())) {
                RepoMovimientos instance = new RepoMovimientos();
                instances.put(componentName, instance);
            }

            /* TECNICOS ADMIN PANEL */
            else if (componentName.equals(TecnicosCRUDController.class.getName())) {
                TecnicosCRUDController instance = new TecnicosCRUDController(instanceOf(TecnicosService.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(TecnicosService.class.getName())) {
                TecnicosService instance = new TecnicosService(instanceOf(RepoTecnicos.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoTecnicos.class.getName())) {
                RepoTecnicos instance = new RepoTecnicos();
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoContactos.class.getName())) {
                RepoContactos instance = new RepoContactos();
                instances.put(componentName, instance);
            }

            /* INCIDENTES CONTROLLER */
            else if (componentName.equals(IncidentesService.class.getName())) {
                IncidentesService instance = new IncidentesService(instanceOf(RepoVisitas.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(RepoVisitas.class.getName())) {
                RepoVisitas instance = new RepoVisitas();
                instances.put(componentName, instance);
            }
        }

        // TODO: Seguir completando con todos los controladores

        return (T) instances.get(componentName);
    }
}
