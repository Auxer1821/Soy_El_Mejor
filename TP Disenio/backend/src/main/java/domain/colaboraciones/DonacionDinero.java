package domain.colaboraciones;

import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="donaciones_dinero")
@NoArgsConstructor
public class DonacionDinero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="monto", nullable = false)
    private Integer monto;

    @Enumerated(value = EnumType.STRING)
    private FrecuenciaDeDonacion frecuencia;

    @Column(name="fecha_de_colaboracion")
    private LocalDateTime fechaDeColaboracion;

    @ManyToOne
    @JoinColumn(name = "colaborador_humano_id")
    private Humana colaboradorHumano;

    @ManyToOne
    @JoinColumn(name ="colaborador_juridico_id")
    private Juridica colaboradorJuridica;

    public DonacionDinero(Integer monto, FrecuenciaDeDonacion frecuencia) {
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.setFechaDeColaboracion(LocalDateTime.now());
    }

    public DonacionDinero(Integer monto, FrecuenciaDeDonacion frecuencia, Humana colaboradorHumano) {
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.colaboradorHumano = colaboradorHumano;
        this.setFechaDeColaboracion(LocalDateTime.now());
    }

    public DonacionDinero(Integer monto, FrecuenciaDeDonacion frecuencia, Juridica juridica) {
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.colaboradorJuridica = juridica;
        this.setFechaDeColaboracion(LocalDateTime.now());
    }

    public DonacionDinero(Integer monto, FrecuenciaDeDonacion frecuencia, LocalDateTime fechaDeColaboracion, Humana colaboradorHumano) {
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.colaboradorHumano = colaboradorHumano;
    }

    public DonacionDinero(Integer monto, FrecuenciaDeDonacion frecuencia, LocalDateTime fechaDeColaboracion, Juridica colaboradorJuridica) {
        this.monto = monto;
        this.frecuencia = frecuencia;
        this.fechaDeColaboracion = fechaDeColaboracion;
        this.colaboradorJuridica = colaboradorJuridica;
    }
}
