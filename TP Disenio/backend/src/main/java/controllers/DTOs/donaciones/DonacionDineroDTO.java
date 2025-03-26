package controllers.DTOs.donaciones;

import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DonacionDineroDTO {
    private String monto;
    private String frecuencia;
    private String mail;

    public static DonacionDineroDTO obtenerDonacionDTO(Context context){
        return new DonacionDineroDTO(
                context.formParam("monto"),
                context.formParam("frecuencia"),
                context.sessionAttribute("email")
        );
    }
}
