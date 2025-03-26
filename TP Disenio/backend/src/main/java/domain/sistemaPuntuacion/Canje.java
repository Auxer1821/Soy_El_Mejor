package domain.sistemaPuntuacion;

import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "canjes")
public class Canje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "beneficio_canjeado")
    private BeneficioOfrecido beneficioCanjeado;

    @Column(name = "beneficio_costo")
    private Integer beneficioCosto;

    @ManyToOne
    @JoinColumn(name = "humana_id")
    private Humana colaboradorHumana;

    @ManyToOne
    @JoinColumn(name = "juridica_id")
    private Juridica colaboradorJuridica;

    @Column(name = "fecha_canje")
    private LocalDate fechaDeCanje;

    public Canje() {
    }

    public Canje(LocalDate fechaDeCanje, BeneficioOfrecido beneficioCanjeado, Humana humana, Juridica juridica) {
        this.beneficioCanjeado = beneficioCanjeado;
        this.fechaDeCanje = fechaDeCanje;
        this.colaboradorHumana = humana;
        this.colaboradorJuridica = juridica;
        this.beneficioCosto = beneficioCanjeado.getPuntosNecesarios();
    }
}
