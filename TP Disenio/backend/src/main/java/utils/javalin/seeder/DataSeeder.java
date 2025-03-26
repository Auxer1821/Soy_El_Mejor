package utils.javalin.seeder;

import config.ServiceLocator;
import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.comunicaciones.Contacto;
import domain.comunicaciones.TipoDeContacto;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.Modelo;
import domain.heladera.entradaSalida.EntradaSalida;
import domain.heladera.entradaSalida.MotivoMovimiento;
import domain.heladera.factory.HeladeraFactory;
import domain.heladera.incidente.Incidente;
import domain.heladera.incidente.IncidenteFactory;
import domain.heladera.incidente.TipoIncidente;
import domain.identificador.Documento;
import domain.identificador.Rubro;
import domain.identificador.TipoDocumento;
import domain.sistemaPuntuacion.Canje;
import domain.tarjetas.Tarjeta;
import domain.ubicaciones.Coordenadas;
import domain.ubicaciones.Direccion;
import domain.ubicaciones.Localidad;
import domain.usuario.colaborador.Admin;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.tecnico.Tecnico;
import domain.usuario.tecnico.cobertura.RadioCobertura;
import domain.usuario.tecnico.cobertura.TipoCobertura;
import domain.usuario.vulnerable.PersonaVulnerable;
import domain.viandas.Vianda;
import org.eclipse.paho.client.mqttv3.MqttException;
import repositorios.RepoGenerico;
import repositorios.colaboradores.RepoHumana;
import utils.passwords.PasswordUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataSeeder {
    private static List<Heladera> heladeras = new ArrayList<>();

    public static void agregarHeladera(Heladera heladera) {
        heladeras.add(heladera);
    }

    public static void init() throws MqttException {

        Direccion direccionCentro2 = new Direccion("Belgrano 600", new Localidad("Villa Lia", "2761"), "Buenos Aires",
                new Coordenadas(-34.126789, -59.433891));
        Direccion direccionSantaColoma2 = new Direccion("Juan B. Justo 1750", new Localidad("Boulogne", "1609"),
                "Buenos Aires", new Coordenadas(-34.502917, -58.576215));
        Direccion direccionSantaColoma = new Direccion("Carlos Gardel 1855", new Localidad("Boulogne", "1609"),
                "Buenos Aires", new Coordenadas(-34.4999471077393, -58.57402877923809));
        Direccion direccionTigre = new Direccion("Martin Pescador 1450", new Localidad("Tigre", "1648"), "Buenos Aires",
                new Coordenadas(-34.42548506275864, -58.58062186765571));
        Direccion direccionCABA2 = new Direccion("Florida 850", new Localidad("CABA", "1000"), "Buenos Aires",
                new Coordenadas(-34.601295, -58.377229));
        Direccion direccionCABA3 = new Direccion("Lavalle 700", new Localidad("CABA", "1000"), "Buenos Aires",
                new Coordenadas(-34.599102, -58.374450));
        Direccion direccionCABA = new Direccion("Av Corrientes 1234", new Localidad("CABA", "1000"), "Buenos Aires",
                new Coordenadas(-34.603722, -58.381592));
        Direccion direccionCordoba2 = new Direccion("Río Bamba 2500", new Localidad("Córdoba", "5000"), "Cordoba",
                new Coordenadas(-31.414720, -64.184233));
        Direccion direccionCordoba3 = new Direccion("Bv. San Juan 750", new Localidad("Córdoba", "5000"), "Cordoba",
                new Coordenadas(-31.421195, -64.182087));
        Direccion direccionCordoba = new Direccion("Av Colón 2500", new Localidad("Córdoba", "5000"), "Cordoba",
                new Coordenadas(-31.4135, -64.18105));
        Direccion direccionMendoza2 = new Direccion("Belgrano 950", new Localidad("Mendoza", "5500"), "Mendoza",
                new Coordenadas(-32.891145, -68.828453));
        Direccion direccionMendoza3 = new Direccion("Arístides Villanueva 600", new Localidad("Mendoza", "5500"),
                "Mendoza", new Coordenadas(-32.893735, -68.832134));
        Direccion direccionMendoza = new Direccion("San Martín 450", new Localidad("Mendoza", "5500"), "Mendoza",
                new Coordenadas(-32.89084, -68.82717));
        Direccion direccionRosario2 = new Direccion("Pellegrini 1400", new Localidad("Rosario", "2000"), "Santa Fe",
                new Coordenadas(-32.947612, -60.642134));
        Direccion direccionRosario3 = new Direccion("Sarmiento 1000", new Localidad("Rosario", "2000"), "Santa Fe",
                new Coordenadas(-32.949475, -60.649318));
        Direccion direccionRosario = new Direccion("Bv. Oroño 900", new Localidad("Rosario", "2000"), "Santa Fe",
                new Coordenadas(-32.94424, -60.65054));
        Direccion direccionSalta2 = new Direccion("Balcarce 500", new Localidad("Salta", "4400"), "Salta",
                new Coordenadas(-24.783761, -65.421982));
        Direccion direccionSalta = new Direccion("Caseros 200", new Localidad("Salta", "4400"), "Salta",
                new Coordenadas(-24.782932, -65.423698));
        Direccion direccionBariloche2 = new Direccion("Frey 300", new Localidad("San Carlos de Bariloche", "8400"),
                "Rio Negro", new Coordenadas(-41.131029, -71.305907));
        Direccion direccionBariloche = new Direccion("Av. Bustillo 2200",
                new Localidad("San Carlos de Bariloche", "8400"), "Rio Negro", new Coordenadas(-41.133472, -71.310277));
        Direccion direccionPosadas2 = new Direccion("Av. Quaranta 2500", new Localidad("Posadas", "3300"), "Misiones",
                new Coordenadas(-27.365123, -55.902762));
        Direccion direccionPosadas = new Direccion("Av Uruguay 1500", new Localidad("Posadas", "3300"), "Misiones",
                new Coordenadas(-27.362703, -55.900036));
        Direccion direccionUshuaia = new Direccion("Gobernador Paz 1000", new Localidad("Ushuaia", "9410"),
                "Tierra del Fuego", new Coordenadas(-54.801912, -68.303047));
        Direccion direccionUshuaia3 = new Direccion("25 de Mayo 300", new Localidad("Ushuaia", "9410"),
                "Tierra del Fuego", new Coordenadas(-54.804123, -68.302923));
        Direccion direccionLaRioja = new Direccion("Pelagio B. Luna 600", new Localidad("La Rioja", "5300"), "La Rioja",
                new Coordenadas(-29.4135, -66.8558));
        Direccion direccionJujuy = new Direccion("Belgrano 400", new Localidad("San Salvador de Jujuy", "4600"),
                "Jujuy", new Coordenadas(-24.185787, -65.299477));
        Direccion direccionJujuy2 = new Direccion("Guemes 300", new Localidad("San Salvador de Jujuy", "4600"), "Jujuy",
                new Coordenadas(-24.187123, -65.297456));
        Direccion direccionNeuquen = new Direccion("Av Argentina 500", new Localidad("Neuquén", "8300"), "Neuquen",
                new Coordenadas(-38.951611, -68.059097));
        Direccion direccionSanJuan2 = new Direccion("Mendoza 1600", new Localidad("San Juan", "5400"), "San Juan",
                new Coordenadas(-31.538123, -68.534789));
        Direccion direccion1 = new Direccion("Av. de Mayo 123", new Localidad("CABA", "1000"), "Buenos Aires",
                new Coordenadas(-34.610224, -58.396013));
        Direccion direccion3 = new Direccion("Avenida Corrientes 3500", new Localidad("Buenos Aires", "1193"),
                "Buenos Aires", new Coordenadas(-34.603684, -58.381559));
        Direccion direccion4 = new Direccion("Avenida Santa Fe 2400", new Localidad("Buenos Aires", "1123"),
                "Buenos Aires", new Coordenadas(-34.589336, -58.397797));
        Direccion direccion5 = new Direccion("Bv. Oroño 2500", new Localidad("Rosario", "2000"), "Santa Fe",
                new Coordenadas(-32.94325, -60.65083));
        Direccion direccion6 = new Direccion("Avenida Rivadavia 4800", new Localidad("Buenos Aires", "1204"),
                "Buenos Aires", new Coordenadas(-34.620834, -58.443327));
        Direccion direccion7 = new Direccion("Av. de los Tilos 300", new Localidad("Salta", "4400"), "Salta",
                new Coordenadas(-24.782932, -65.423698));
        Direccion direccion8 = new Direccion("Av. del Libertador 8000", new Localidad("La Plata", "1900"),
                "Buenos Aires", new Coordenadas(-34.91743, -57.95504));
        Direccion direccion9 = new Direccion("Av. Sarmiento 200", new Localidad("Ushuaia", "9410"), "Tierra del Fuego",
                new Coordenadas(-54.801912, -68.303047));
        Direccion direccion10 = new Direccion("Av. Bartolomé Mitre 100", new Localidad("La Rioja", "5300"), "La Rioja",
                new Coordenadas(-29.41109, -66.8558));
        Direccion direccion11 = new Direccion("Avenida Cabildo 3200", new Localidad("Buenos Aires", "1429"),
                "Buenos Aires", new Coordenadas(-34.563539, -58.456256));
        Direccion direccion12 = new Direccion("Calle Principal 150", new Localidad("Neuquén", "8300"), "Neuquen",
                new Coordenadas(-38.951611, -68.059097));
        Direccion direccion13 = new Direccion("Av. 9 de Julio 600", new Localidad("Bariloche", "8400"), "Rio Negro",
                new Coordenadas(-41.135258, -71.308825));
        Direccion direccion14 = new Direccion("Av. Mitre 300", new Localidad("Posadas", "3300"), "Misiones",
                new Coordenadas(-27.362703, -55.900036));
        Direccion direccion15 = new Direccion("Calle San Juan 200", new Localidad("Tigre", "1648"), "Buenos Aires",
                new Coordenadas(-34.42548506275864, -58.58062186765571));
        Direccion direccion16 = new Direccion("Avenida Belgrano 1800", new Localidad("Buenos Aires", "1093"),
                "Buenos Aires", new Coordenadas(-34.612962, -58.390879));
        Direccion direccion17 = new Direccion("Calle Florida 700", new Localidad("Buenos Aires", "1040"),
                "Buenos Aires", new Coordenadas(-34.603723, -58.377602));
        Direccion direccion18 = new Direccion("Calle Juramento 2700", new Localidad("Buenos Aires", "1428"),
                "Buenos Aires", new Coordenadas(-34.559789, -58.455444));
        Direccion direccion19 = new Direccion("Calle Defensa 1500", new Localidad("Buenos Aires", "1143"),
                "Buenos Aires", new Coordenadas(-34.621419, -58.373993));
        Direccion direccion20 = new Direccion("Avenida de Mayo 900", new Localidad("Buenos Aires", "1084"),
                "Buenos Aires", new Coordenadas(-34.608712, -58.382116));
        Direccion direccion21 = new Direccion("Calle Lavalle 1300", new Localidad("Buenos Aires", "1052"),
                "Buenos Aires", new Coordenadas(-34.605158, -58.384392));
        Direccion direccion22 = new Direccion("Avenida Callao 1500", new Localidad("Buenos Aires", "1022"),
                "Buenos Aires", new Coordenadas(-34.598295, -58.407369));
        Direccion direccion23 = new Direccion("Calle Pueyrredón 1100", new Localidad("Buenos Aires", "1414"),
                "Buenos Aires", new Coordenadas(-34.596732, -58.396603));
        Direccion direccion24 = new Direccion("Avenida Alvear 2200", new Localidad("Buenos Aires", "1124"),
                "Buenos Aires", new Coordenadas(-34.587595, -58.391286));
        Direccion direccion25 = new Direccion("Avenida San Juan 2300", new Localidad("Buenos Aires", "1232"),
                "Buenos Aires", new Coordenadas(-34.622448, -58.406619));
        Direccion direccion26 = new Direccion("Avenida Juan B. Justo 5600", new Localidad("Buenos Aires", "1416"),
                "Buenos Aires", new Coordenadas(-34.591551, -58.488415));
        Direccion direccion27 = new Direccion("Avenida Independencia 4200", new Localidad("Buenos Aires", "1222"),
                "Buenos Aires", new Coordenadas(-34.622319, -58.416852));

        // Creación de las entidades
        PersonaVulnerable persona1 = new PersonaVulnerable("Ana", "Martínez", LocalDateTime.now().minusYears(30),
                LocalDateTime.now(), direccionTigre, null);
        PersonaVulnerable persona2 = new PersonaVulnerable("Luis", "Martínez", LocalDateTime.now().minusYears(25),
                LocalDateTime.now(), direccionTigre, List.of(persona1));
        PersonaVulnerable persona3 = new PersonaVulnerable("María", "Fernández", LocalDateTime.now().minusYears(40),
                LocalDateTime.now(), direccion5, null);
        PersonaVulnerable persona4 = new PersonaVulnerable("José", "Fernández", LocalDateTime.now().minusYears(35),
                LocalDateTime.now(), direccion5, null);
        PersonaVulnerable persona5 = new PersonaVulnerable("Clara", "Fernández", LocalDateTime.now().minusYears(28),
                LocalDateTime.now(), direccion5, List.of(persona3, persona4));
        PersonaVulnerable persona6 = new PersonaVulnerable("Pedro", "Ramirez", LocalDateTime.now().minusYears(22),
                LocalDateTime.now(), direccion7, null);
        PersonaVulnerable persona7 = new PersonaVulnerable("Laura", "Ramirez", LocalDateTime.now().minusYears(32),
                LocalDateTime.now(), direccion7, List.of(persona6));
        PersonaVulnerable persona8 = new PersonaVulnerable("Ricardo", "Diaz", LocalDateTime.now().minusYears(29),
                LocalDateTime.now(), direccion8, null);
        PersonaVulnerable persona9 = new PersonaVulnerable("Silvia", "Torres", LocalDateTime.now().minusYears(45),
                LocalDateTime.now(), direccion9, null);
        PersonaVulnerable persona10 = new PersonaVulnerable("Diego", "Pérez", LocalDateTime.now().minusYears(50),
                LocalDateTime.now(), direccion10, null);
        PersonaVulnerable persona11 = new PersonaVulnerable("Gloria", "Cruz", LocalDateTime.now().minusYears(27),
                LocalDateTime.now(), direccion12, null);
        PersonaVulnerable persona12 = new PersonaVulnerable("Alberto", "Cruz", LocalDateTime.now().minusYears(33),
                LocalDateTime.now(), direccion12, List.of(persona11));
        PersonaVulnerable persona13 = new PersonaVulnerable("Rosa", "Bermudez", LocalDateTime.now().minusYears(36),
                LocalDateTime.now(), direccion13, null);
        PersonaVulnerable persona14 = new PersonaVulnerable("Ezequiel", "Córdoba", LocalDateTime.now().minusYears(38),
                LocalDateTime.now(), direccion14, null);
        PersonaVulnerable persona15 = new PersonaVulnerable("Lucía", "Maldonado", LocalDateTime.now().minusYears(24),
                LocalDateTime.now(), direccion15, null);

        Admin admin = new Admin("admin", "admin", "admin@example.com", direccion1);
        // Colaboradores usando direcciones de Buenos Aires, CABA, y otras provincias
        Humana colaborador1 = new Humana("carlosecheverria", PasswordUtils.hashPassword("password123"),
                "carlos@example.com", "12345678", TipoDocumento.DNI, "Carlos", "Echeverria", direccionCABA2);
        Humana colaborador2 = new Humana("mariaperez", PasswordUtils.hashPassword("password123"), "maria@example.com",
                "87654321", TipoDocumento.DNI, "Maria", "Perez", direccionSantaColoma2);
        Humana colaborador3 = new Humana("luciagomez", PasswordUtils.hashPassword("password123"), "lucia@example.com",
                "11223344", TipoDocumento.DNI, "Lucia", "Gomez", direccionJujuy);
        Humana colaborador4 = new Humana("jorgelopez", PasswordUtils.hashPassword("password123"), "jorge@example.com",
                "44332211", TipoDocumento.DNI, "Jorge", "Lopez", direccionCordoba);
        Humana colaborador5 = new Humana("sofiamartinez", PasswordUtils.hashPassword("password123"),
                "sofia@example.com", "55667788", TipoDocumento.DNI, "Sofia", "Martinez", direccionMendoza2);
        Humana colaborador6 = new Humana("juanfernandez", PasswordUtils.hashPassword("password123"), "juan@example.com",
                "99887766", TipoDocumento.DNI, "Juan", "Fernandez", direccionRosario3);
        Humana colaborador7 = new Humana("anasanchez", PasswordUtils.hashPassword("password123"), "ana@example.com",
                "66778899", TipoDocumento.DNI, "Ana", "Sanchez", direccionSalta2);
        Humana colaborador8 = new Humana("pabloramirez", PasswordUtils.hashPassword("password123"), "pablo@example.com",
                "44556677", TipoDocumento.DNI, "Pablo", "Ramirez", direccionBariloche);
        Humana colaborador9 = new Humana("fernandadiaz", PasswordUtils.hashPassword("password123"),
                "fernanda@example.com", "12312312", TipoDocumento.DNI, "Fernanda", "Diaz", direccionPosadas2);
        Humana colaborador10 = new Humana("luiscastro", PasswordUtils.hashPassword("password123"), "luis@example.com",
                "32132132", TipoDocumento.DNI, "Luis", "Castro", direccionUshuaia3);
        Humana colaborador11 = new Humana("ricardofort", PasswordUtils.hashPassword("password123"),
                "rickyfort@example.com", "32132132", TipoDocumento.DNI, "Ricardo", "Fort", direccionUshuaia3, 100000D);

        // Entidades Juridicas usando direcciones restantes
        Juridica juridica1 = new Juridica("techsolutions", PasswordUtils.hashPassword("password123"),
                "tech@example.com", "20-12345678-9", "Tech Solutions S.A.", "Empresa", Rubro.EMPRESA,
                direccionSantaColoma);
        Juridica juridica2 = new Juridica("libreriacentral", PasswordUtils.hashPassword("password123"),
                "libreria@example.com", "20-87654321-0", "Libreria Central S.R.L.", "Empresa", Rubro.EMPRESA,
                direccionCentro2);
        Juridica juridica3 = new Juridica("serviciosmedicos", PasswordUtils.hashPassword("password123"),
                "servicios@example.com", "20-11223344-8", "Servicios Medicos Rosario", "Empresa", Rubro.EMPRESA,
                direccionRosario);
        Juridica juridica4 = new Juridica("cordobafoods", PasswordUtils.hashPassword("password123"),
                "cordoba@example.com", "20-44332211-5", "Cordoba Foods", "Empresa", Rubro.EMPRESA, direccionCordoba2);
        Juridica juridica5 = new Juridica("construccionesmendoza", PasswordUtils.hashPassword("password123"),
                "construcciones@example.com", "20-55667788-2", "Construcciones Mendoza", "Empresa", Rubro.EMPRESA,
                direccionMendoza);
        Juridica juridica6 = new Juridica("inversionessanjuan", PasswordUtils.hashPassword("password123"),
                "inversiones@example.com", "20-99887766-7", "Inversiones San Juan S.A.", "Empresa", Rubro.EMPRESA,
                direccionSanJuan2);
        Juridica juridica7 = new Juridica("automotorescolapinto", PasswordUtils.hashPassword("password123"),
                "automotores@example.com", "20-66778899-3", "Automotores Colapinto", "Empresa", Rubro.EMPRESA,
                direccionCABA);
        Juridica juridica8 = new Juridica("turismopatagonia", PasswordUtils.hashPassword("password123"),
                "turismo@example.com", "20-44556677-6", "Turismo Patagonia", "Empresa", Rubro.EMPRESA,
                direccionBariloche2);
        Juridica juridica9 = new Juridica("mueblesposadas", PasswordUtils.hashPassword("password123"),
                "muebles@example.com", "20-12312312-1", "Muebles Posadas", "Empresa", Rubro.EMPRESA, direccionPosadas);
        Juridica juridica10 = new Juridica("ushuaialogistics", PasswordUtils.hashPassword("password123"),
                "ushuaia@example.com", "20-32132132-4", "Ushuaia Logistics", "Empresa", Rubro.EMPRESA,
                direccionUshuaia);

        // Documento instances
        Documento documento1 = new Documento("44185512", TipoDocumento.DNI);
        Documento documento2 = new Documento("30234856", TipoDocumento.DNI);
        Documento documento3 = new Documento("28903465", TipoDocumento.DNI);
        Documento documento4 = new Documento("25346789", TipoDocumento.DNI);
        Documento documento5 = new Documento("33678921", TipoDocumento.DNI);
        Documento documento6 = new Documento("41234567", TipoDocumento.DNI);
        Documento documento7 = new Documento("29876543", TipoDocumento.DNI);
        Documento documento8 = new Documento("35678923", TipoDocumento.DNI);
        Documento documento9 = new Documento("42789012", TipoDocumento.DNI);
        Documento documento10 = new Documento("36985214", TipoDocumento.DNI);

        // Coordenadas para ubicaciones en distintas ciudades de Argentina
        Coordenadas ubicacion1 = new Coordenadas(-34.603723, -58.377232); // Buenos Aires
        Coordenadas ubicacion2 = new Coordenadas(-31.420083, -64.188776); // Córdoba
        Coordenadas ubicacion3 = new Coordenadas(-32.890844, -68.827171); // Mendoza
        Coordenadas ubicacion4 = new Coordenadas(-38.951611, -68.059097); // Neuquén
        Coordenadas ubicacion5 = new Coordenadas(-24.782932, -65.423197); // Salta
        Coordenadas ubicacion6 = new Coordenadas(-31.536676, -68.525703); // San Juan
        Coordenadas ubicacion7 = new Coordenadas(-34.611778, -58.417308); // La Plata
        Coordenadas ubicacion8 = new Coordenadas(-27.451134, -58.986548); // Corrientes
        Coordenadas ubicacion9 = new Coordenadas(-26.808285, -65.217590); // Tucumán
        Coordenadas ubicacion10 = new Coordenadas(-37.321674, -59.133161); // Tandil

        // Tecnico instances
        Tecnico tecnico1 = new Tecnico("20123456789", "Luciano", "Rocchetta", documento1, ubicacion1);
        Tecnico tecnico2 = new Tecnico("30123456780", "Mariana", "Lopez", documento2, ubicacion2);
        Tecnico tecnico3 = new Tecnico("40123456781", "Carlos", "Ramirez", documento3, ubicacion3);
        Tecnico tecnico4 = new Tecnico("5040053", "Ana", "Martinez", documento4, ubicacion4);
        Tecnico tecnico5 = new Tecnico("6040054", "Jorge", "Gomez", documento5, ubicacion5);
        Tecnico tecnico6 = new Tecnico("7040055", "Laura", "Fernandez", documento6, ubicacion6);
        Tecnico tecnico7 = new Tecnico("8040056", "Diego", "Sanchez", documento7, ubicacion7);
        Tecnico tecnico8 = new Tecnico("9040057", "Sofia", "Gutierrez", documento8, ubicacion8);
        Tecnico tecnico9 = new Tecnico("10040058", "Pablo", "Rojas", documento9, ubicacion9);
        Tecnico tecnico10 = new Tecnico("11040059", "Valeria", "Castro", documento10, ubicacion10);
        Tecnico tecnico = new Tecnico(
                "20445527220",
                "Bruno",
                "Juan Sartori",
                new Documento("44552722", TipoDocumento.DNI),
                new RadioCobertura(10D, new Direccion("Av. Rivadavia 1000")),
                new Coordenadas(-34.599102, -58.374450));

        Modelo modelo1 = new Modelo("LG LSXS26326S", -3.0, 19.5, 23);
        Modelo modelo2 = new Modelo("Frigidaire FFTR1814QS", -2.9, 18.5, 22);
        Modelo modelo3 = new Modelo("Kenmore 73025", -2.6, 17.8, 20);
        Modelo modelo4 = new Modelo("Maytag MRT711BZDE", -2.5, 19.0, 21);
        Modelo modelo5 = new Modelo("Bosch B36CL80ENS", -2.5, 18.5, 20);
        Modelo modelo6 = new Modelo("GE GNE25JMKES", -2.8, 19.0, 21);
        Modelo modelo7 = new Modelo("Frigidaire FFHB2750TS", -2.7, 18.5, 20);
        Modelo modelo8 = new Modelo("LG LFXC22526S", -3.0, 19.0, 22);
        Modelo modelo9 = new Modelo("Whirlpool WRF550SDFZ", -2.5, 17.0, 20);
        Modelo modelo10 = new Modelo("Samsung RT38", -2.0, 18.0, 21);

        Heladera heladera1 = HeladeraFactory.create(direccionCABA3, "Heladera UTN - CABA", modelo1, 10);
        Heladera heladera2 = HeladeraFactory.create(direccionCordoba3, "Heladera UTN - Córdoba", modelo2, 2);
        Heladera heladera3 = HeladeraFactory.create(direccionMendoza3, "Heladera UTN - Mendoza", modelo3, 5);
        Heladera heladera4 = HeladeraFactory.create(direccionSalta, "Heladera UTN - Salta", modelo4, 21);
        Heladera heladera5 = HeladeraFactory.create(direccionBariloche, "Heladera UTN - Bariloche", modelo5, 7);

        /*
         * Heladera heladera6 = HeladeraFactory.create(direccionPosadas,
         * "Heladera UTN - Posadas", modelo6, 13);
         * Heladera heladera7 = HeladeraFactory.create(direccionRosario2,
         * "Heladera UTN - Ushuaia", modelo7, 20);
         * Heladera heladera8 = HeladeraFactory.create(direccionLaRioja,
         * "Heladera UTN - La Rioja", modelo8, 16);
         * Heladera heladera9 = HeladeraFactory.create(direccionJujuy2,
         * "Heladera UTN - Jujuy", modelo9, 10);
         * Heladera heladera10 = HeladeraFactory.create(direccionNeuquen,
         * "Heladera UTN - Neuquén", modelo10, 6);
         * Heladera heladera11 = HeladeraFactory.create(direccion19,
         * "Heladera UTN - CABA 2", modelo1, 11);
         * Heladera heladera13 = HeladeraFactory.create(direccion20,
         * "Heladera UTN - CABA 3", modelo2, 13);
         * Heladera heladera14 = HeladeraFactory.create(direccion21,
         * "Heladera UTN - CABA 4", modelo3, 15);
         * Heladera heladera15 = HeladeraFactory.create(direccion22,
         * "Heladera UTN - CABA 5", modelo4, 14);
         * Heladera heladera16 = HeladeraFactory.create(direccion23,
         * "Heladera UTN - CABA 6", modelo5, 12);
         * Heladera heladera17 = HeladeraFactory.create(direccion24,
         * "Heladera UTN - CABA 7", modelo6, 18);
         * Heladera heladera18 = HeladeraFactory.create(direccion25,
         * "Heladera UTN - CABA 8", modelo7, 19);
         * Heladera heladera19 = HeladeraFactory.create(direccion3,
         * "Heladera UTN - CABA 9", modelo8, 8);
         * Heladera heladera20 = HeladeraFactory.create(direccion4,
         * "Heladera UTN - CABA 10", modelo9, 9);
         * Heladera heladera21 = HeladeraFactory.create(direccion26,
         * "Heladera UTN - CABA 11", modelo10, 3);
         * Heladera heladera22 = HeladeraFactory.create(direccion27,
         * "Heladera UTN - CABA 12", modelo1, 6);
         * Heladera heladera23 = HeladeraFactory.create(direccion6,
         * "Heladera UTN - CABA 13", modelo2, 20);
         * Heladera heladera24 = HeladeraFactory.create(direccion11,
         * "Heladera UTN - CABA 14", modelo3, 17);
         * Heladera heladera25 = HeladeraFactory.create(direccion16,
         * "Heladera UTN - CABA 15", modelo4, 14);
         * Heladera heladera26 = HeladeraFactory.create(direccion17,
         * "Heladera UTN - CABA 16", modelo5, 14);
         * Heladera heladera27 = HeladeraFactory.create(direccion18,
         * "Heladera UTN - CABA 17", modelo6, 12);
         */

        agregarHeladera(heladera1);
        agregarHeladera(heladera2);
        agregarHeladera(heladera3);
        agregarHeladera(heladera4);
        agregarHeladera(heladera5);

        /*
         * agregarHeladera(heladera6);
         * agregarHeladera(heladera7);
         * agregarHeladera(heladera8);
         * agregarHeladera(heladera9);
         * agregarHeladera(heladera10);
         * agregarHeladera(heladera11);
         * agregarHeladera(heladera13);
         * agregarHeladera(heladera14);
         * agregarHeladera(heladera15);
         * agregarHeladera(heladera16);
         * agregarHeladera(heladera17);
         * agregarHeladera(heladera18);
         * agregarHeladera(heladera19);
         * agregarHeladera(heladera20);
         * agregarHeladera(heladera21);
         * agregarHeladera(heladera22);
         * agregarHeladera(heladera23);
         * agregarHeladera(heladera24);
         * agregarHeladera(heladera25);
         * agregarHeladera(heladera26);
         * agregarHeladera(heladera27);
         */

        Vianda vianda1 = new Vianda("Milanesa con puré", heladera1, 550, 450, LocalDate.of(2024, 10, 10),
                LocalDateTime.of(2024, 9, 25, 10, 30), colaborador1);
        Vianda vianda2 = new Vianda("Pollo asado con ensalada", heladera1, 600, 500, LocalDate.of(2024, 10, 12),
                LocalDateTime.of(2024, 9, 27, 11, 0), colaborador2);
        Vianda vianda3 = new Vianda("Empanadas de carne", heladera2, 400, 300, LocalDate.of(2024, 10, 8),
                LocalDateTime.of(2024, 9, 24, 14, 15), colaborador3);
        Vianda vianda4 = new Vianda("Tarta de espinaca", heladera3, 350, 350, LocalDate.of(2024, 10, 11),
                LocalDateTime.of(2024, 9, 26, 12, 45), colaborador4);
        Vianda vianda5 = new Vianda("Canelones de ricota", heladera4, 500, 400, LocalDate.of(2024, 10, 9),
                LocalDateTime.of(2024, 9, 23, 9, 30), colaborador5);
        /*
         * Vianda vianda6 = new Vianda("Hamburguesas con papas", heladera5, 700, 600,
         * LocalDate.of(2024, 10, 10), LocalDateTime.of(2024, 9, 25, 13, 0),
         * colaborador6);
         * Vianda vianda7 = new Vianda("Fideos con tuco", heladera6, 450, 400,
         * LocalDate.of(2024, 10, 8), LocalDateTime.of(2024, 9, 22, 15, 45),
         * colaborador7);
         * Vianda vianda8 = new Vianda("Guiso de lentejas", heladera7, 600, 550,
         * LocalDate.of(2024, 10, 12), LocalDateTime.of(2024, 9, 27, 12, 15),
         * colaborador8);
         * Vianda vianda9 = new Vianda("Pizza de muzzarella", heladera8, 800, 700,
         * LocalDate.of(2024, 10, 7), LocalDateTime.of(2024, 9, 21, 17, 30),
         * colaborador9);
         * Vianda vianda10 = new Vianda("Sopa de verduras", heladera9, 300, 300,
         * LocalDate.of(2024, 10, 11), LocalDateTime.of(2024, 9, 26, 10, 0),
         * colaborador10);
         * Vianda vianda11 = new Vianda("Churrasco con papas", heladera10, 650, 500,
         * LocalDate.of(2024, 10, 10), LocalDateTime.of(2024, 9, 25, 18, 30),
         * colaborador1);
         * Vianda vianda12 = new Vianda("Ravioles de ricota", heladera1, 550, 400,
         * LocalDate.of(2024, 10, 9), LocalDateTime.of(2024, 9, 23, 11, 30),
         * colaborador2);
         * Vianda vianda13 = new Vianda("Ensalada César", heladera2, 300, 350,
         * LocalDate.of(2024, 10, 8), LocalDateTime.of(2024, 9, 24, 14, 0),
         * colaborador3);
         * Vianda vianda14 = new Vianda("Wok de vegetales", heladera3, 400, 400,
         * LocalDate.of(2024, 10, 12), LocalDateTime.of(2024, 9, 27, 15, 0),
         * colaborador4);
         * Vianda vianda15 = new Vianda("Milanesa de soja con ensalada", heladera4, 450,
         * 500, LocalDate.of(2024, 10, 11), LocalDateTime.of(2024, 9, 26, 10, 45),
         * colaborador5);
         * Vianda vianda16 = new Vianda("Lasagna de carne", heladera1, 700, 600,
         * LocalDate.of(2024, 10, 24), LocalDateTime.of(2024, 10, 24, 10, 0),
         * colaborador6);
         * Vianda vianda17 = new Vianda("Ñoquis con salsa rosa", heladera2, 550, 500,
         * LocalDate.of(2024, 10, 25), LocalDateTime.of(2024, 10, 25, 12, 30),
         * colaborador7);
         * Vianda vianda18 = new Vianda("Risotto de hongos", heladera3, 650, 550,
         * LocalDate.of(2024, 10, 26), LocalDateTime.of(2024, 10, 26, 14, 0),
         * colaborador8);
         * Vianda vianda19 = new Vianda("Cazuela de pollo", heladera4, 600, 550,
         * LocalDate.of(2024, 10, 27), LocalDateTime.of(2024, 10, 27, 11, 45),
         * colaborador9);
         * Vianda vianda20 = new Vianda("Tortilla de papas", heladera5, 500, 450,
         * LocalDate.of(2024, 10, 28), LocalDateTime.of(2024, 10, 28, 9, 30),
         * colaborador10);
         * Vianda vianda21 = new Vianda("Pollo a la parrilla", heladera6, 750, 700,
         * LocalDate.of(2024, 10, 24), LocalDateTime.of(2024, 10, 24, 13, 15),
         * colaborador1);
         * Vianda vianda22 = new Vianda("Spaghetti a la boloñesa", heladera7, 600, 550,
         * LocalDate.of(2024, 10, 25), LocalDateTime.of(2024, 10, 25, 14, 30),
         * colaborador2);
         * Vianda vianda23 = new Vianda("Rollo de carne con puré", heladera8, 700, 600,
         * LocalDate.of(2024, 10, 26), LocalDateTime.of(2024, 10, 26, 16, 0),
         * colaborador3);
         * Vianda vianda24 = new Vianda("Ensalada griega", heladera9, 350, 300,
         * LocalDate.of(2024, 10, 27), LocalDateTime.of(2024, 10, 27, 11, 0),
         * colaborador4);
         * Vianda vianda25 = new Vianda("Fajitas de pollo", heladera10, 550, 500,
         * LocalDate.of(2024, 10, 28), LocalDateTime.of(2024, 10, 28, 10, 45),
         * colaborador5);
         * Vianda vianda26 = new Vianda("Salmón con papas", heladera1, 800, 750,
         * LocalDate.of(2024, 10, 29), LocalDateTime.of(2024, 10, 29, 12, 15),
         * colaborador6);
         * Vianda vianda27 = new Vianda("Guiso de arroz", heladera2, 500, 450,
         * LocalDate.of(2024, 10, 29), LocalDateTime.of(2024, 10, 29, 13, 45),
         * colaborador7);
         * Vianda vianda28 = new Vianda("Lentejas con chorizo", heladera3, 600, 550,
         * LocalDate.of(2024, 10, 30), LocalDateTime.of(2024, 10, 30, 14, 30),
         * colaborador8);
         * Vianda vianda29 = new Vianda("Costillas de cerdo", heladera4, 750, 700,
         * LocalDate.of(2024, 10, 30), LocalDateTime.of(2024, 10, 30, 16, 0),
         * colaborador9);
         * Vianda vianda30 = new Vianda("Tarta de jamón y queso", heladera5, 450, 400,
         * LocalDate.of(2024, 10, 24), LocalDateTime.of(2024, 10, 24, 9, 30),
         * colaborador10);
         */

        // BeneficioOfrecidos
        BeneficioOfrecido beneficio1 = new BeneficioOfrecido("Computadora portátil", 3500, "/img/pc_sin_fondo.png",
                juridica1, "Una laptop moderna con alto rendimiento, ideal para trabajar y estudiar.");
        BeneficioOfrecido beneficio2 = new BeneficioOfrecido("Set de útiles escolares", 1500,
                "/img/utiles_escolares.png", juridica2,
                "Un completo set con todo lo necesario para la vuelta al colegio.");
        BeneficioOfrecido beneficio3 = new BeneficioOfrecido("Chequeo médico completo", 4500, "/img/chequeo_medico.jpg",
                juridica3, "Un examen médico integral para garantizar tu salud.");
        BeneficioOfrecido beneficio4 = new BeneficioOfrecido("Canasta alimentaria", 2000, "/img/canasta_s_fondo.png",
                juridica4, "Productos básicos para una alimentación saludable.");
        BeneficioOfrecido beneficio5 = new BeneficioOfrecido("Materiales de construcción", 4000,
                "/img/materiales_construccion_s_fondo.png", juridica5,
                "Todo lo que necesitas para llevar a cabo tu proyecto de construcción.");
        BeneficioOfrecido beneficio6 = new BeneficioOfrecido("Consultoría financiera", 3000,
                "/img/consultor-financiero.jpg", juridica6,
                "Asesoramiento financiero para optimizar tus inversiones y ahorros.");
        BeneficioOfrecido beneficio7 = new BeneficioOfrecido("Servicio de mecánica básica", 2500,
                "/img/servicio_mecanico.png", juridica7,
                "Mantenimiento básico para tu vehículo, incluye revisión general.");
        BeneficioOfrecido beneficio8 = new BeneficioOfrecido("Tour guiado por la Patagonia", 5000,
                "/img/tour_patagonia.jpg", juridica8,
                "Una experiencia única para conocer los paisajes más increíbles de la Patagonia.");
        BeneficioOfrecido beneficio9 = new BeneficioOfrecido("Set de muebles de oficina", 3200,
                "/img/muebles_oficina.png", juridica9, "Equipamiento ergonómico y moderno para tu espacio de trabajo.");
        BeneficioOfrecido beneficio10 = new BeneficioOfrecido("Envío de logística internacional", 4700,
                "/img/envios_internacionales.png", juridica10,
                "Servicio de transporte para envíos internacionales, con seguimiento en tiempo real.");
        BeneficioOfrecido beneficio11 = new BeneficioOfrecido("Impresora multifunción", 3000,
                "/img/impresora_multifuncion.png", juridica1,
                "Impresora de última tecnología, con funciones de impresión, escaneo y copia.");
        BeneficioOfrecido beneficio12 = new BeneficioOfrecido("Colección de libros", 1800, "/img/libros_cocina.png",
                juridica2,
                "Una selección de libros de cocina para aprender a realizar las mejores recetas saludables.");
        BeneficioOfrecido beneficio13 = new BeneficioOfrecido("Vacunación anual", 2200, "/img/vacunacion_anuakl.jpg",
                juridica3, "Protege tu salud con el esquema de vacunación actualizado.");
        BeneficioOfrecido beneficio14 = new BeneficioOfrecido("Descuento en supermercado", 1200,
                "/img/promo_en_super_png.png", juridica4, "Descuentos exclusivos en tus compras del supermercado.");
        BeneficioOfrecido beneficio15 = new BeneficioOfrecido("Asesoría en inversiones", 3500,
                "/img/asesoria_en_inversiones.png", juridica6,
                "Recibe orientación profesional para tomar mejores decisiones financieras.");

        // Tarjetas
        Tarjeta tarjeta1 = new Tarjeta(colaborador1);
        Tarjeta tarjeta2 = new Tarjeta(colaborador2);
        Tarjeta tarjeta3 = new Tarjeta(colaborador3);
        Tarjeta tarjeta4 = new Tarjeta(colaborador4);
        Tarjeta tarjeta5 = new Tarjeta(colaborador5);
        Tarjeta tarjeta6 = new Tarjeta(colaborador6);
        Tarjeta tarjeta7 = new Tarjeta(colaborador7);
        Tarjeta tarjeta8 = new Tarjeta(colaborador8);
        Tarjeta tarjeta9 = new Tarjeta(colaborador9);
        Tarjeta tarjeta10 = new Tarjeta(colaborador10);
        Tarjeta tarjeta11 = new Tarjeta(persona2);
        Tarjeta tarjeta12 = new Tarjeta(persona5);
        Tarjeta tarjeta13 = new Tarjeta(persona12);
        Tarjeta tarjeta14 = new Tarjeta(persona7);
        Tarjeta tarjeta15 = new Tarjeta(persona8);
        Tarjeta tarjeta16 = new Tarjeta(persona9);
        Tarjeta tarjeta17 = new Tarjeta(persona10);
        Tarjeta tarjeta18 = new Tarjeta(persona13);
        Tarjeta tarjeta19 = new Tarjeta(persona14);
        Tarjeta tarjeta20 = new Tarjeta(persona15);

        // Canjes
        Canje canje1 = new Canje(LocalDate.of(2024, 10, 1), beneficio1, colaborador1, null);
        Canje canje2 = new Canje(LocalDate.of(2024, 10, 2), beneficio2, colaborador1, null);
        Canje canje3 = new Canje(LocalDate.of(2024, 10, 3), beneficio3, colaborador1, null);
        Canje canje4 = new Canje(LocalDate.of(2024, 10, 4), beneficio4, colaborador1, null);
        Canje canje5 = new Canje(LocalDate.of(2024, 10, 5), beneficio5, colaborador1, null);
        Canje canje6 = new Canje(LocalDate.of(2024, 10, 6), beneficio6, colaborador6, null);
        Canje canje7 = new Canje(LocalDate.of(2024, 10, 7), beneficio7, colaborador7, null);
        Canje canje8 = new Canje(LocalDate.of(2024, 10, 8), beneficio8, colaborador8, null);
        Canje canje9 = new Canje(LocalDate.of(2024, 10, 9), beneficio9, colaborador9, null);
        Canje canje10 = new Canje(LocalDate.of(2024, 10, 10), beneficio10, colaborador10, null);

        /*
         * Incidente incidente1 = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA,
         * heladera4, LocalDateTime.now(), colaborador3);
         * Incidente incidente2 = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA,
         * heladera1, LocalDateTime.now(), colaborador2);
         * Incidente incidente3 = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA,
         * heladera13, LocalDateTime.now(), colaborador10);
         * Incidente incidente4 = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA,
         * heladera24, LocalDateTime.now(), colaborador7);
         * Incidente incidente5 = IncidenteFactory.create(TipoIncidente.FALLA_TECNICA,
         * heladera9, LocalDateTime.now(), colaborador1);
         */

        /*
         * EntradaSalida entradaSalida1 = new EntradaSalida(heladera2,
         * MotivoMovimiento.ENTRADA, 3);
         * EntradaSalida entradaSalida2 = new EntradaSalida(heladera5,
         * MotivoMovimiento.SALIDA, 2);
         * EntradaSalida entradaSalida3 = new EntradaSalida(heladera13,
         * MotivoMovimiento.ENTRADA, 5);
         * EntradaSalida entradaSalida4 = new EntradaSalida(heladera19,
         * MotivoMovimiento.SALIDA, 6);
         * EntradaSalida entradaSalida5 = new EntradaSalida(heladera26,
         * MotivoMovimiento.ENTRADA, 12);
         * EntradaSalida entradaSalida6 = new EntradaSalida(heladera19,
         * MotivoMovimiento.SALIDA, 6);
         * EntradaSalida entradaSalida7 = new EntradaSalida(heladera21,
         * MotivoMovimiento.ENTRADA, 12);
         * EntradaSalida entradaSalida8 = new EntradaSalida(heladera17,
         * MotivoMovimiento.SALIDA, 6);
         * EntradaSalida entradaSalida9 = new EntradaSalida(heladera10,
         * MotivoMovimiento.ENTRADA, 12);
         * EntradaSalida entradaSalida10 = new EntradaSalida(heladera3,
         * MotivoMovimiento.ENTRADA, 6);
         * EntradaSalida entradaSalida11 = new EntradaSalida(heladera2,
         * MotivoMovimiento.SALIDA, 12);
         */

        RepoGenerico repositorio = ServiceLocator.instanceOf(RepoGenerico.class);
        RepoHumana repoHumana = ServiceLocator.instanceOf(RepoHumana.class);

        try {
            repoHumana.beginTransaction();
            repoHumana.persist(colaborador1);
            repoHumana.persist(colaborador2);
            repoHumana.persist(colaborador3);
            repoHumana.persist(colaborador4);
            repoHumana.persist(colaborador5);
            repoHumana.persist(colaborador6);
            repoHumana.persist(colaborador7);
            repoHumana.persist(colaborador8);
            repoHumana.persist(colaborador9);
            repoHumana.persist(colaborador10);
            repoHumana.persist(colaborador11);
            repoHumana.commitTransaction();
        } catch (Exception e) {
            repoHumana.rollbackTransaction();
            throw new RuntimeException(e);
        }

        try {
            repositorio.beginTransaction();
            repositorio.persist(modelo1);
            repositorio.persist(modelo2);
            repositorio.persist(modelo3);
            repositorio.persist(modelo4);
            repositorio.persist(modelo5);
            repositorio.persist(modelo6);
            repositorio.persist(modelo7);
            repositorio.persist(modelo8);
            repositorio.persist(modelo9);
            repositorio.persist(modelo10);

            repositorio.persist(admin);

            repositorio.persist(juridica1);
            repositorio.persist(juridica2);
            repositorio.persist(juridica3);
            repositorio.persist(juridica4);
            repositorio.persist(juridica5);
            repositorio.persist(juridica6);
            repositorio.persist(juridica7);
            repositorio.persist(juridica8);
            repositorio.persist(juridica9);
            repositorio.persist(juridica10);

            repositorio.persist(persona1);
            repositorio.persist(persona2);
            repositorio.persist(persona3);
            repositorio.persist(persona4);
            repositorio.persist(persona5);
            repositorio.persist(persona6);
            repositorio.persist(persona7);
            repositorio.persist(persona8);
            repositorio.persist(persona9);
            repositorio.persist(persona10);
            repositorio.persist(persona11);
            repositorio.persist(persona12);
            repositorio.persist(persona13);
            repositorio.persist(persona14);
            repositorio.persist(persona15);

            int i = 0;
            while (i < heladeras.size()) {
                HeladeraFactory.registrarHeladera(heladeras.get(i));
                repositorio.persist(heladeras.get(i));
                i++;
            }

            repositorio.persist(vianda1);
            repositorio.persist(vianda2);
            repositorio.persist(vianda3);
            repositorio.persist(vianda4);
            repositorio.persist(vianda5);
            /*
             * repositorio.persist(vianda6);
             * repositorio.persist(vianda7);
             * repositorio.persist(vianda8);
             * repositorio.persist(vianda9);
             * repositorio.persist(vianda10);
             * repositorio.persist(vianda11);
             * repositorio.persist(vianda12);
             * repositorio.persist(vianda13);
             * repositorio.persist(vianda14);
             * repositorio.persist(vianda15);
             * repositorio.persist(vianda16);
             * repositorio.persist(vianda17);
             * repositorio.persist(vianda18);
             * repositorio.persist(vianda19);
             * repositorio.persist(vianda20);
             * repositorio.persist(vianda21);
             * repositorio.persist(vianda22);
             * repositorio.persist(vianda23);
             * repositorio.persist(vianda24);
             * repositorio.persist(vianda25);
             * repositorio.persist(vianda26);
             * repositorio.persist(vianda27);
             * repositorio.persist(vianda28);
             * repositorio.persist(vianda29);
             * repositorio.persist(vianda30);
             */

            repositorio.persist(beneficio1);
            repositorio.persist(beneficio2);
            repositorio.persist(beneficio3);
            repositorio.persist(beneficio4);
            repositorio.persist(beneficio5);
            repositorio.persist(beneficio6);
            repositorio.persist(beneficio7);
            repositorio.persist(beneficio8);
            repositorio.persist(beneficio9);
            repositorio.persist(beneficio10);
            repositorio.persist(beneficio11);
            repositorio.persist(beneficio12);
            repositorio.persist(beneficio13);
            repositorio.persist(beneficio14);
            repositorio.persist(beneficio15);

            /*
             * repositorio.persist(incidente1);
             * repositorio.persist(incidente2);
             * repositorio.persist(incidente3);
             * repositorio.persist(incidente4);
             * repositorio.persist(incidente5);
             */
            /*
             * repositorio.persist(entradaSalida1);
             * repositorio.persist(entradaSalida2);
             * repositorio.persist(entradaSalida3);
             * repositorio.persist(entradaSalida4);
             * repositorio.persist(entradaSalida5);
             * repositorio.persist(entradaSalida6);
             * repositorio.persist(entradaSalida7);
             * repositorio.persist(entradaSalida8);
             * repositorio.persist(entradaSalida9);
             * repositorio.persist(entradaSalida10);
             * repositorio.persist(entradaSalida11);
             */

            repositorio.persist(tarjeta1);
            repositorio.persist(tarjeta2);
            repositorio.persist(tarjeta3);
            repositorio.persist(tarjeta4);
            repositorio.persist(tarjeta5);
            repositorio.persist(tarjeta6);
            repositorio.persist(tarjeta7);
            repositorio.persist(tarjeta8);
            repositorio.persist(tarjeta9);
            repositorio.persist(tarjeta10);
            repositorio.persist(tarjeta11);
            repositorio.persist(tarjeta12);
            repositorio.persist(tarjeta13);
            repositorio.persist(tarjeta14);
            repositorio.persist(tarjeta15);
            repositorio.persist(tarjeta16);
            repositorio.persist(tarjeta17);
            repositorio.persist(tarjeta18);
            repositorio.persist(tarjeta19);
            repositorio.persist(tarjeta20);

            repositorio.persist(canje1);
            repositorio.persist(canje2);
            repositorio.persist(canje3);
            repositorio.persist(canje4);
            repositorio.persist(canje5);
            repositorio.persist(canje6);
            repositorio.persist(canje7);
            repositorio.persist(canje8);
            repositorio.persist(canje9);
            repositorio.persist(canje10);

            repositorio.persist(tecnico);
            repositorio.persist(tecnico1);
            repositorio.persist(tecnico2);
            repositorio.persist(tecnico3);
            repositorio.persist(tecnico4);
            repositorio.persist(tecnico5);
            repositorio.persist(tecnico6);
            repositorio.persist(tecnico7);
            repositorio.persist(tecnico8);
            repositorio.persist(tecnico9);
            repositorio.persist(tecnico10);
            repositorio.commitTransaction();
        } catch (Exception e) {
            repositorio.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }
}
