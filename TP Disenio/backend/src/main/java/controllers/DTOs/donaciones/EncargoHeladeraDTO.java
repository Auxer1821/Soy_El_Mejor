package controllers.DTOs.donaciones;

import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.core.SpringVersion;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@Getter
public class EncargoHeladeraDTO {
    private String nombre;
    private Long id_modelo;
    private String direccion;
    private String localidad;
    private String provincia;
    private String lat;
    private String lng;

    public static EncargoHeladeraDTO obtenerEncargoHeladeraDTO(Context context){
        return new EncargoHeladeraDTO(
                context.formParam("nombre"),
                Long.valueOf(Objects.requireNonNull(context.formParam("modeloId"))),
                context.formParam("direccion"),
                context.formParam("loc_nombre"),
                context.formParam("prov_nombre"),
                context.formParam("lat"),
                context.formParam("lng"));
    }

}
