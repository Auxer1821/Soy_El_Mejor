package domain.auditoria;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auditorias")
@NoArgsConstructor
public class Auditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String tipo_dato;

    @Column
    private String id_dato;

    @Column
    private LocalDateTime fecha;

    public Auditor(String tipo_dato, String id_dato) {
        this.tipo_dato = tipo_dato;
        this.id_dato = id_dato;
    }
}
