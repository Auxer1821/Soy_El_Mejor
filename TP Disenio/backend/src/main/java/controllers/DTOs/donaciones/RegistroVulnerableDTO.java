package controllers.DTOs.donaciones;

import controllers.DTOs.colaboradores.UsuarioDTO;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
@Data
@AllArgsConstructor
@Getter
public class RegistroVulnerableDTO {
    private List<String> nombre;
    private List<String> apellido;
    private List<String> tipoDocumento;
    private List<String> nro_documento;
    private String provincia;
    private String localidad;
    private String domicilio;
    private List<String> fecha_nacimiento;

    public static RegistroVulnerableDTO obtenerRegistroVulnerableDTO(Context context){
        return new RegistroVulnerableDTO(
                context.formParams("nombre"),
                context.formParams("apellido"),
                context.formParams("tipo_documento"),
                context.formParams("numero_documento"),
                context.formParam("provincia"),
                context.formParam("nombreLocalidad"),
                context.formParam("direccion"),
                context.formParams("dob")
        );
    }
}
