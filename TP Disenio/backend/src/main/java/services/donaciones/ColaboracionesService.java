package services.donaciones;

import Converters.DateTimeConverter;
import controllers.DTOs.donaciones.*;
import domain.colaboraciones.*;
import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.colaboraciones.beneficios.TipoRubro;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.Modelo;
import domain.heladera.entradaSalida.EntradaSalida;
import domain.heladera.entradaSalida.MotivoMovimiento;
import domain.heladera.factory.HeladeraFactory;
import domain.sistemaPuntuacion.ColaboracionRealizada;
import domain.sistemaPuntuacion.MotivoColaboracionRealizada;
import domain.tarjetas.Tarjeta;
import domain.ubicaciones.Coordenadas;
import domain.ubicaciones.Direccion;
import domain.ubicaciones.Localidad;
import domain.ubicaciones.comunidades.Comunidad;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.colaborador.RolUsuario;
import domain.usuario.tecnico.Tecnico;
import domain.usuario.tecnico.buscadorTecnicoCercano.BuscadorTecnicoCercano;
import domain.usuario.vulnerable.PersonaVulnerable;
import domain.usuario.vulnerable.VulnerableFactory;
import domain.viandas.Vianda;
import io.javalin.http.Context;
import repositorios.RepoGenerico;
import repositorios.colaboraciones.RepoComunidades;
import repositorios.colaboraciones.RepoColaboraciones;
import repositorios.colaboraciones.RepoRegistroPersonasVulnerables;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.heladera.RepoHeladera;
import repositorios.tarjetas.RepoTarjetas;
import repositorios.tecnico.RepoTecnicos;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ColaboracionesService {
    private RepoGenerico repoGenerico;
    private RepoHeladera repoHeladera;
    private RepoJuridica repoJuridica;
    private RepoHumana repoHumana;
    private RepoComunidades repoComunidades;
    private RepoTarjetas repoTarjetas;
    private RepoColaboraciones repoColaboraciones;
    private RepoRegistroPersonasVulnerables repoRegistroPersonasVulnerables;
    private RepoTecnicos repoTecnicos;

    public ColaboracionesService(RepoGenerico repoGenerico, RepoHeladera repoHeladera, RepoJuridica repoJuridica, RepoHumana repoHumana, RepoComunidades repoComunidades, RepoTarjetas repoTarjetas, RepoColaboraciones repoColaboraciones, RepoRegistroPersonasVulnerables repoRegistroPersonasVulnerables, RepoTecnicos repoTecnicos) {
        this.repoGenerico = repoGenerico;
        this.repoHeladera = repoHeladera;
        this.repoJuridica = repoJuridica;
        this.repoHumana = repoHumana;
        this.repoComunidades = repoComunidades;
        this.repoTarjetas = repoTarjetas;
        this.repoColaboraciones = repoColaboraciones;
        this.repoRegistroPersonasVulnerables = repoRegistroPersonasVulnerables;
        this.repoTecnicos = repoTecnicos;
    }

    public Comunidad verificarComunidad(DonarViandaDTO donarViandaDTO) {
        Optional<Comunidad> comunidad = repoComunidades.buscarPorNombreYDireccion(donarViandaDTO.getComunidad(), donarViandaDTO.getDireccionComunidad());

        if (comunidad.isPresent()) {
            return comunidad.get();
        } else {
            Comunidad nueva_comunidad = new Comunidad(donarViandaDTO.getComunidad(), donarViandaDTO.getDireccionComunidad());

            repoGenerico.persist(nueva_comunidad);

            return verificarComunidad(donarViandaDTO);
        }
    }

    @Transactional
    public void crearDonacionViandaHeladera(Context context) {
        try {
            DonarViandaDTO donacion = DonarViandaDTO.obtenerDonacionHDTO(context);

            repoGenerico.beginTransaction();

            Heladera heladera = repoHeladera.buscarPorId(context.formParam("heladeraId"));

            Humana humana = repoHumana.buscarPorID(context.sessionAttribute("id_usuario"));
            Tarjeta tarjetaHumana = repoTarjetas.buscarPorIdHumana(humana.getId());

            Vianda vianda = new Vianda(context.formParam("comida"),
                    heladera,
                    Integer.valueOf(donacion.getCalorias()),
                    Integer.valueOf(donacion.getPeso()),
                    LocalDate.parse(Objects.requireNonNull(context.formParam("fecha_caducidad"))),
                    LocalDateTime.now(),
                    humana
            );

            DonacionVianda donacionVianda = new DonacionVianda(humana, LocalDateTime.now(), vianda, EstadoDonacion.ACEPTADA);

            EntradaSalida entrada_heladera = new EntradaSalida(heladera, MotivoMovimiento.ENTRADA, 1);
            SolicitudApertura solicitud = new SolicitudApertura(LocalDateTime.now(), tarjetaHumana.getCodigo(), heladera, donacionVianda);

            repoGenerico.persist(vianda);
            repoGenerico.persist(donacionVianda);
            repoGenerico.persist(entrada_heladera);
            repoGenerico.persist(solicitud);
            repoGenerico.commitTransaction();
        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public void crearDonacionViandaComunidad(Context context) {
        DonarViandaDTO donacion = DonarViandaDTO.obtenerDonacionCDTO(context);

        repoGenerico.beginTransaction();

        try {
            Comunidad comunidad_a_donar = verificarComunidad(donacion);

            Humana humana = repoHumana.buscarPorID(context.sessionAttribute("id_usuario"));

            Vianda vianda = new Vianda(context.formParam("comida"),
                    comunidad_a_donar,
                    Integer.valueOf(donacion.getCalorias()),
                    Integer.valueOf(donacion.getPeso()),
                    LocalDate.parse(Objects.requireNonNull(context.formParam("fecha_caducidad"))),
                    LocalDateTime.now(),
                    humana
            );

            DonacionVianda donacionVianda = new DonacionVianda(humana, LocalDateTime.now(), vianda);

            repoGenerico.persist(vianda);
            repoGenerico.persist(donacionVianda);
            repoGenerico.commitTransaction();
        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public void crearDonacionDinero(Context context) {
        DonacionDineroDTO donacion = DonacionDineroDTO.obtenerDonacionDTO(context);

        FrecuenciaDeDonacion frecuencia = FrecuenciaDeDonacion.valueOf(donacion.getFrecuencia().toUpperCase());

        RolUsuario rol = RolUsuario.valueOf(context.sessionAttribute("rol"));

        try {
            repoGenerico.beginTransaction();

            DonacionDinero donacionDinero = new DonacionDinero(
                    Integer.valueOf(donacion.getMonto()),
                    frecuencia
            );

            ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada(MotivoColaboracionRealizada.DONACIONDINERO, donacionDinero);
            Double puntos = colaboracionRealizada.puntosGanados();

            switch (rol) {
                case JURIDICA -> {
                    Juridica juridica = repoJuridica.buscarPorID(context.sessionAttribute("id_usuario"));

                    donacionDinero.setColaboradorJuridica(juridica);

                    juridica.setPuntos_adquiridos(juridica.getPuntos_adquiridos() + puntos);

                    repoGenerico.persist(donacionDinero);
                }
                case HUMANA -> {
                    Humana humana = repoHumana.buscarPorID(context.sessionAttribute("id_usuario"));

                    donacionDinero.setColaboradorHumano(humana);

                    humana.setPuntos_adquiridos(humana.getPuntos_adquiridos() + puntos);

                    repoGenerico.persist(donacionDinero);
                }
            }
            repoGenerico.commitTransaction();

        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException(e);
        }

    }

    public DistribucionViandas buscarDistribucion(String id) {
        Optional<DistribucionViandas> distribucionViandas = repoColaboraciones.buscarPorId(Long.valueOf(id));

        if (distribucionViandas.isPresent()) {
            if (distribucionViandas.get().getHumana() == null) {
                return distribucionViandas.get();
            } else {
                throw new RuntimeException("La distribucion solicitada ya le pertenece a un colaborador.");
            }
        } else {
            throw new RuntimeException("La distribucion solicitada no existe.");
        }
    }

    public void asignarDistribucion(String codDistribucion, String id_humana) {
        Humana humana = repoHumana.buscarPorID(id_humana);

        Optional<DistribucionViandas> distribucionVianda = repoColaboraciones.buscarPorId(Long.valueOf(codDistribucion));

        repoColaboraciones.beginTransaction();
        if (distribucionVianda.isPresent()) {
            if (distribucionVianda.get().getHumana() == null) {
                repoColaboraciones.asignarColaborador(Long.valueOf(codDistribucion), humana);

                try {
                    DistribucionViandas distribucion = distribucionVianda.get();

                    Tarjeta tarjetaHumana = repoTarjetas.buscarPorIdHumana(distribucion.getHumana().getId());

                    Heladera heladeraOrigen = repoHeladera.buscarPorId(distribucion.getHeladeraOrigen().getId());
                    Heladera heladeraDestino = repoHeladera.buscarPorId(distribucion.getHeladeraDestino().getId());

                    EntradaSalida salida_heladera = new EntradaSalida(heladeraOrigen, MotivoMovimiento.SALIDA, distribucion.getCantidadViandas());
                    EntradaSalida entrada_heladera = new EntradaSalida(heladeraDestino, MotivoMovimiento.ENTRADA, distribucion.getCantidadViandas());

                    SolicitudApertura solicitudOrigen = new SolicitudApertura(LocalDateTime.now(), tarjetaHumana.getCodigo(), heladeraOrigen, distribucion);
                    SolicitudApertura solicitudDestino = new SolicitudApertura(LocalDateTime.now(), tarjetaHumana.getCodigo(), heladeraDestino, distribucion);

                    repoColaboraciones.persist(solicitudOrigen);
                    repoColaboraciones.persist(solicitudDestino);

                    repoColaboraciones.persist(salida_heladera);
                    repoColaboraciones.persist(entrada_heladera);

                    heladeraOrigen.registrarMovimiento(salida_heladera);
                    heladeraDestino.registrarMovimiento(entrada_heladera);
                    repoColaboraciones.commitTransaction();

                } catch (Exception e) {
                    repoColaboraciones.rollbackTransaction();
                    throw new RuntimeException("Error al avisarle a la heladera sobre la distribucion.");
                }
            } else {
                repoColaboraciones.rollbackTransaction();
                throw new RuntimeException("La distribucion solicitada ya le pertenece a un colaborador.");
            }
        } else {
            repoColaboraciones.rollbackTransaction();
            throw new RuntimeException("La distribucion solicitada no existe.");
        }
    }

    public void registrarPersonaVulnerable(Context context) {
        RegistroVulnerableDTO vulnerableDTO = RegistroVulnerableDTO.obtenerRegistroVulnerableDTO(context);
        try {
            repoGenerico.beginTransaction();

            PersonaVulnerable vulnerable = VulnerableFactory.create(
                    vulnerableDTO.getNombre().get(0),
                    vulnerableDTO.getApellido().get(0),
                    DateTimeConverter.convertSinTiempo(vulnerableDTO.getFecha_nacimiento().get(0)),
                    LocalDateTime.now(),
                    new Direccion(vulnerableDTO.getDomicilio(), new Localidad(vulnerableDTO.getLocalidad()), vulnerableDTO.getProvincia())
            );

            for (int i = 1; i < vulnerableDTO.getNombre().size(); i++) {
                PersonaVulnerable vulnerableHijo = VulnerableFactory.create(
                        vulnerableDTO.getNombre().get(i),
                        vulnerableDTO.getApellido().get(i),
                        DateTimeConverter.convertSinTiempo(vulnerableDTO.getFecha_nacimiento().get(i)),
                        LocalDateTime.now(),
                        new Direccion(vulnerableDTO.getDomicilio(), new Localidad(vulnerableDTO.getLocalidad()), vulnerableDTO.getProvincia())
                );
                repoGenerico.persist(vulnerableHijo);

                vulnerable.agregarHijo(vulnerableHijo);
            }
            Humana colaborador = repoHumana.buscarPorID(context.sessionAttribute("id_usuario"));
            RegistroDePersonasVulnerables registro = new RegistroDePersonasVulnerables(vulnerable, colaborador);

            ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada(MotivoColaboracionRealizada.TARJETAENTREGADA, registro);

            Double puntos = colaboracionRealizada.puntosGanados();
            colaborador.setPuntos_adquiridos(colaborador.getPuntos_adquiridos() + puntos);

            repoGenerico.persist(colaboracionRealizada);
            repoGenerico.persist(vulnerable);
            repoGenerico.persist(registro);
            repoGenerico.persist(colaborador);
            repoGenerico.commitTransaction();

        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public void crearBeneficio(BeneficioDTO beneficioDTO, String id) {
        repoGenerico.beginTransaction();

        try {
            Juridica juridica = repoJuridica.buscarPorID(id);

            BeneficioOfrecido nuevo_beneficio = new BeneficioOfrecido(
                    beneficioDTO.getNombre(),
                    beneficioDTO.getDescripcion(),
                    TipoRubro.valueOf(beneficioDTO.getRubro()),
                    Integer.valueOf(beneficioDTO.getPuntosNecesarios()),
                    beneficioDTO.getImagenPath(),
                    juridica
            );

            repoGenerico.persist(nuevo_beneficio);
            repoGenerico.commitTransaction();
        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException("Ocurrio un error inesperado. Por favor intentelo mas tarde");
        }
    }


    public void crearEncargoHeladera(Context context) {
        EncargoHeladeraDTO encargoHeladeraDTO = EncargoHeladeraDTO.obtenerEncargoHeladeraDTO(context);

        Modelo modelo = repoHeladera.obtenerModelo(encargoHeladeraDTO.getId_modelo());
        Direccion direccion_nueva = new Direccion(encargoHeladeraDTO.getDireccion(),
                new Localidad(encargoHeladeraDTO.getLocalidad()),
                encargoHeladeraDTO.getProvincia(),
                new Coordenadas(Double.valueOf(encargoHeladeraDTO.getLat()), Double.valueOf(encargoHeladeraDTO.getLng())));

        List<Tecnico> tecnicos = repoTecnicos.buscarTodos();

        try {
            repoGenerico.beginTransaction();

            Optional<Tecnico> tecnicoOptional = BuscadorTecnicoCercano.buscarTecnicoCercano(tecnicos, direccion_nueva.getCoordenadas());

            if (tecnicoOptional.isPresent()) {

                System.out.println(tecnicoOptional.get());

                Heladera heladera = HeladeraFactory.create(
                        direccion_nueva,
                        encargoHeladeraDTO.getNombre(),
                        modelo,
                        Estado.FUNCIONAMIENTO,
                        LocalDateTime.now(),
                        List.of(),
                        0
                );

                Juridica juridica = repoJuridica.buscarPorID(context.sessionAttribute("id_usuario"));
                EncargoHeladera encargo = new EncargoHeladera(LocalDateTime.now(), heladera, juridica);

                repoGenerico.persist(heladera);
                repoGenerico.persist(encargo);

                HeladeraFactory.registrarHeladera(heladera);
                repoGenerico.commitTransaction();
            } else {
                repoGenerico.rollbackTransaction();
                throw new RuntimeException("No se han encontrado tecnicos activos cercano a su posicion. Por favor seleccione otro lugar.");
            }

        } catch (Exception e) {
            repoGenerico.rollbackTransaction();
            throw new RuntimeException(e);
        }
    }

    public List<Modelo> obtenerModelosHeladera() {
        return repoHeladera.obtenerModelos();
    }
}
