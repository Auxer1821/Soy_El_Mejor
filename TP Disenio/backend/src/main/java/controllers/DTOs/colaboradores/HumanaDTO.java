package controllers.DTOs.colaboradores;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HumanaDTO {
    private String nombre;
    private String apellido;
    private String tipoDocumento;
    private String nroDocumento;
    private String provincia;
    private String localidad;
    private String direccion;
    private String dob;
    private List<String> tiposContacto;
    private List<String> datosContacto;
    private String username;
    private String password;
    private String email;

    public HumanaDTO(String nombre, String apellido, String tipoDocumento, String nroDocumento, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.email = email;
    }
}
