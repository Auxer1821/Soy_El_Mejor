package domain.usuario.colaborador;

import domain.forms.FormularioCompletado;
import domain.ubicaciones.Direccion;
import lombok.AllArgsConstructor;
import lombok.Data;
import repositorios.RepoGenerico;
import utils.passwords.PasswordUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Table(name="usuarios", uniqueConstraints = {@UniqueConstraint(name = "UK_email", columnNames = "email"), @UniqueConstraint(name = "UK_username", columnNames = "username")})
@Entity  @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ROL", discriminatorType = DiscriminatorType.STRING)
public abstract class UsuarioPersistente {
    @Id
    private String id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Embedded
    private Direccion direccion;

    @Column
    private Double puntos_adquiridos;

    @Column(name = "ROL", insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private RolUsuario rolUsuario;

    @Column(nullable = false)
    private LocalDateTime fecha_alta;

}
