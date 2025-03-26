package controllers.DTOs.donaciones;

import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DonarViandaDTO {
    private String comida;
    private String fecha_caducidad;
    private String comunidad;
    private String direccionComunidad;
    private String heladeraID;
    private String calorias;
    private String peso;

    public DonarViandaDTO(String comida, String fecha_caducidad, String comunidad, String direccionComunidad, String calorias, String peso) {
        this.comida = comida;
        this.fecha_caducidad = fecha_caducidad;
        this.comunidad = comunidad;
        this.direccionComunidad = direccionComunidad;
        this.calorias = calorias;
        this.peso = peso;
    }

    public DonarViandaDTO(String comida, String fecha_caducidad, String heladeraID, String calorias, String peso) {
        this.comida = comida;
        this.fecha_caducidad = fecha_caducidad;
        this.heladeraID = heladeraID;
        this.calorias = calorias;
        this.peso = peso;
    }

    public static DonarViandaDTO obtenerDonacionHDTO(Context context){
        return new DonarViandaDTO(
                context.formParam("comida"),
                context.formParam("fecha_caducidad"),
                context.formParam("id_heladera"),
                context.formParam("calorias"),
                context.formParam("peso")
        );
    }

    public static DonarViandaDTO obtenerDonacionCDTO(Context context){
        return new DonarViandaDTO(
                context.formParam("comida"),
                context.formParam("fecha_caducidad"),
                context.formParam("nombre_comunidad"),
                context.formParam("direccion_comunidad"),
                context.formParam("calorias"),
                context.formParam("peso")
        );
    }
}
