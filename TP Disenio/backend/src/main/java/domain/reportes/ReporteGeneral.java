package domain.reportes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reportes")
@AllArgsConstructor
@NoArgsConstructor
public class ReporteGeneral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ubicacion")
    private String path;

    @Column(name = "fecha_realizacion")
    private LocalDate fecha;

    public ReporteGeneral(String path, LocalDate fecha) {
        this.path = path;
        this.fecha = fecha;
    }
}
