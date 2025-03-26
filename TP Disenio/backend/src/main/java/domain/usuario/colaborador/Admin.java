package domain.usuario.colaborador;

import domain.ubicaciones.Direccion;
import lombok.*;
import utils.passwords.PasswordUtils;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@ToString
@DiscriminatorValue("ADMIN")
public class Admin extends UsuarioPersistente{
    public Admin(String username, String password, String email, Direccion direccion) {
        this.setId(UUID.randomUUID().toString());
        this.setUsername(username);
        this.setPassword(PasswordUtils.hashPassword(password));
        this.setEmail(email);
        this.setDireccion(direccion);
        this.setRolUsuario(RolUsuario.ADMIN);
        this.setFecha_alta(LocalDateTime.now());
    }
}
