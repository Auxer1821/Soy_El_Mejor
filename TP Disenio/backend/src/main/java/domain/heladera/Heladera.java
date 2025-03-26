package domain.heladera;

import config.ServiceLocator;
import domain.colaboraciones.SolicitudApertura;
import domain.comunicaciones.Notificador;
import domain.heladera.entradaSalida.EntradaSalida;
import domain.heladera.gestores.GestorSuscriptores;
import domain.heladera.incidente.Incidente;
import domain.heladera.sensores.SensorPersistente;
import domain.suscripciones.SuscripcionViandasFaltantes;
import domain.tarjetas.Tarjeta;
import domain.ubicaciones.Direccion;
import domain.viandas.Vianda;
import lombok.*;
import services.notificadores.NotificadorDeTecnicos;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "heladeras")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Heladera {
    @Id
    private String id;

    @Column(name = "nombre", unique = true)
    private String nombre;

    @Embedded
    private Direccion direccion;

    @Column(name = "fecha_funcionamiento")
    private LocalDateTime fechaFuncionamiento;

    @Column(name = "cant_viandas")
    private Integer cantidadViandas;

    @Column(name = "max_cant_viandas")
    private Integer maxCantidadViandas;

    @ManyToOne
    @JoinColumn(name = "modelo_id", referencedColumnName = "id")
    private Modelo modelo;

    @Column(name = "temperatura_actual")
    private Double temperaturaActual;

    @Enumerated(EnumType.STRING)
    private Estado estado = Estado.FUNCIONAMIENTO;

    @Column(name = "dias_funcionando")
    private Integer diasFuncionamiento;

    @Embedded
    private GestorSuscriptores gestorSuscriptores;

    @OneToMany(mappedBy = "heladera")
    private List<Vianda> viandas = new ArrayList<Vianda>();

    @OneToMany(mappedBy = "heladera")
    private List<AperturaFehaciente> aperturasFehacientes = new ArrayList<>();

    @OneToMany(mappedBy = "heladera")
    private List<SolicitudApertura> solicitudesAperturas = new ArrayList<SolicitudApertura>();

    @OneToMany(mappedBy = "heladera")
    private List<Incidente> listaIncidentes = new ArrayList<Incidente>();

    @OneToMany(mappedBy = "heladera")
    private List<EntradaSalida> listaEntradasSalidas = new ArrayList<EntradaSalida>();

    @OneToMany(mappedBy = "heladera")
    private List<SensorPersistente> sensores = new ArrayList<SensorPersistente>();

    public Heladera(Direccion direccion, String nombre) {
        this.id = UUID.randomUUID().toString();
        this.direccion = direccion;
        this.nombre = nombre;
        this.diasFuncionamiento = 0;
        this.fechaFuncionamiento = LocalDateTime.now();
    }

    public void agregarSuscriptorVacio(SuscripcionViandasFaltantes suscripcionViandasFaltantes) {
        this.gestorSuscriptores.agregarSuscriptorViandasFaltantes(suscripcionViandasFaltantes);
    }

    public Boolean estaVacia() {
        return this.cantViandasActual() == 0;
    }

    public Boolean estaEnFuncionamiento() {
        return this.estado == Estado.FUNCIONAMIENTO;
    }

    public Integer cantViandasActual() {
        return viandas.size();
    }

    public Boolean temperaturaEntreLimites() {
        return modelo.getTemperaturaMinima() < temperaturaActual && modelo.getTemperaturaMaxima() > temperaturaActual;
    }

    public void setEstado(Estado estado) {
        if (estado != Estado.FUNCIONAMIENTO) {
            this.setDiasFuncionamiento(this.diasTotalesActivos());
        }
        this.estado = estado;
    }

    public Integer diasTotalesActivos() {
        int cantidadUltimosDiasActivos = 0;

        if (this.estaEnFuncionamiento() && this.fechaFuncionamiento != null) {
            cantidadUltimosDiasActivos = (int) ChronoUnit.DAYS.between(this.fechaFuncionamiento, LocalDateTime.now());
        }

        int diasFuncionamientoActuales = (this.diasFuncionamiento != null) ? this.diasFuncionamiento : 0;

        return diasFuncionamientoActuales + cantidadUltimosDiasActivos;
    }

    public List<SolicitudApertura> puedeAbrir(Tarjeta tarjeta) {
        LocalDateTime hace3horas = LocalDateTime.now().minusHours(3);

        List<SolicitudApertura> solicitudes = this.getSolicitudesAperturas().stream()
                .filter(solicitudApertura -> {
                    boolean coincideCodigo = Objects.equals(solicitudApertura.getCodigo_tarjeta(), tarjeta.getCodigo());
                    boolean esReciente = solicitudApertura.getFechaActual().isAfter(hace3horas);

                    return coincideCodigo && esReciente;
                })
                .toList();

        if (solicitudes.isEmpty()) {
            System.out.println(
                    "No hay solicitudes disponibles en las últimas 3 horas para la tarjeta " + tarjeta.getCodigo());
        } else {
            System.out.println("Se encontraron " + solicitudes.size() + " solicitudes válidas.");
        }

        return solicitudes;
    }

    public void registrarApertura(AperturaFehaciente aperturaFehaciente) {
        this.aperturasFehacientes.add(aperturaFehaciente);
    }

    public void aniadirVianda(Vianda vianda) {
        this.viandas.add(vianda);
        this.cantidadViandas = viandas.size();
    }

    public void registrarIncidente(Incidente incidente) {
        listaIncidentes.add(incidente);
    }

    public void registrarMovimiento(EntradaSalida es) {
        listaEntradasSalidas.add(es);
    }

    public void registrarSolicitudApertura(SolicitudApertura Solicitud) {
        solicitudesAperturas.add(Solicitud);
    }

    public void vaciarSolicitudes() {
        solicitudesAperturas.clear();
    }

    public int getMaxCantidadViandas() {
        return this.modelo.getMaxCantidadViandas();
    }

    public void agregarSensor(SensorPersistente sensor) {
        this.sensores.add(sensor);
    }
}
