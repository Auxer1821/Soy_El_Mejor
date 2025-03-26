package domain.heladera.entradaSalida;

import domain.heladera.Heladera;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.mail.Header;
import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "entradas_salidas")
@NoArgsConstructor
@AllArgsConstructor
public class EntradaSalida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "heladera_id", referencedColumnName = "id")
    private Heladera heladera;

    @Enumerated(EnumType.STRING)
    private MotivoMovimiento motivo;

    @Column(name = "fecha_de_movimiento")
    private LocalDateTime fechaMovimiento = LocalDateTime.now();

    @Column(name = "cantidad_viandas")
    private Integer cantidadViandas;

    public EntradaSalida(Heladera heladera, MotivoMovimiento motivo, Integer cantidadViandas) {
        this.heladera = heladera;
        this.motivo = motivo;
        this.cantidadViandas = cantidadViandas;
    }
}
