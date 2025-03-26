package domain.usuario.vulnerable;

import domain.ubicaciones.Direccion;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class VulnerableFactory {
    public static PersonaVulnerable create(String nombre, String apellido, LocalDateTime fechaDeNacimiento, LocalDateTime fechaRegistro, Direccion direccion){
        return new PersonaVulnerable(nombre, apellido, fechaDeNacimiento, fechaRegistro, direccion, null);
    }
}
