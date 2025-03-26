package domain.sistemaPuntuacion;

import domain.colaboraciones.*;
import utils.PropertiesManager;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table( name = "colaboraciones_realizadas")
public class ColaboracionRealizada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "indice")
    private Double indice;

    @Column(name = "coeficiente")
    private Double coeficiente;

    @Enumerated (EnumType.STRING)
    @Column(name = "motivo", nullable = false)
    private MotivoColaboracionRealizada motivo;

    @OneToOne
    @JoinColumn(name = "donacion_vianda_id", referencedColumnName ="id")
    private DonacionVianda donacionVianda;

    @OneToOne
    @JoinColumn(name = "donacion_dinero_id", referencedColumnName ="id")
    private DonacionDinero donacionDinero;

    @OneToOne
    @JoinColumn(name = "encargo_heladera_id", referencedColumnName ="id")
    private EncargoHeladera encargoHeladera;

    @OneToOne
    @JoinColumn(name = "registro_persona_id", referencedColumnName = "id")
    private RegistroDePersonasVulnerables tarjetaEntregada;

    @OneToOne
    @JoinColumn(name = "distribucion_viandas_id", referencedColumnName = "id")
    private DistribucionViandas viandasDistribuidas;

    public void establecerParametros(){
        PropertiesManager propertiesManager = new PropertiesManager("properties/coeficientesPuntos.properties");

        switch (this.getMotivo())
        {
            case DONACIONDINERO -> {
                this.indice = this.getDonacionDinero().getMonto().doubleValue();
                this.coeficiente = propertiesManager.getPropertyDouble("PESOS_DONADOS");
            }
            case DONACIONVIANDA -> {
                this.indice = (double) 1;
                this.coeficiente = propertiesManager.getPropertyDouble("VIANDAS_DONADAS");
            }
            case ENCARGOHELADERA -> {
                this.indice = (double) (this.getEncargoHeladera().getHeladera().diasTotalesActivos() / 30);
                this.coeficiente = propertiesManager.getPropertyDouble("CANTIDAD_HELADERAS_ACTIVAS");
            }
            case TARJETAENTREGADA -> {
                this.indice = (double) 1;
                this.coeficiente = propertiesManager.getPropertyDouble("TARJETAS_REPARTIDAS");
            }
            case DISTRIBUCIONVIANDA -> {
                this.indice = this.getViandasDistribuidas().getCantidadViandas().doubleValue();
                this.coeficiente = propertiesManager.getPropertyDouble("VIANDAS_DISTRIBUIDAS");
            }
            default -> {
                this.indice = (double) 0;
                this.coeficiente = (double) 0;
            }
        }
    }

    public Double getTotal(){
        return this.indice * this.coeficiente;
    }

    public Double puntosGanados(){
        this.establecerParametros();
        return getTotal();
    }

    public void setRegistroDePersona(RegistroDePersonasVulnerables colaboracion) {
    }

    public ColaboracionRealizada(MotivoColaboracionRealizada motivo, DonacionVianda donacionVianda) {
        this.motivo = motivo;
        this.donacionVianda = donacionVianda;
    }

    public ColaboracionRealizada(MotivoColaboracionRealizada motivo, DonacionDinero donacionDinero) {
        this.motivo = motivo;
        this.donacionDinero = donacionDinero;
    }

    public ColaboracionRealizada(MotivoColaboracionRealizada motivo, RegistroDePersonasVulnerables tarjetaEntregada) {
        this.motivo = motivo;
        this.tarjetaEntregada = tarjetaEntregada;
    }

    public ColaboracionRealizada(MotivoColaboracionRealizada motivo, DistribucionViandas viandasDistribuidas) {
        this.motivo = motivo;
        this.viandasDistribuidas = viandasDistribuidas;
    }
}
