package controllers.DTOs.donaciones;

import domain.colaboraciones.beneficios.BeneficioOfrecido;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
public class BeneficioDTO {
    private String id;
    private String nombre;
    private String rubro;
    private String descripcion;
    private String puntosNecesarios;
    private String imagenPath;
    private Boolean puedeCanjear = false;

    public BeneficioDTO(String nombre, String descripcion, String puntosNecesarios, String imagenPath) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
        this.imagenPath = imagenPath;
    }

    public BeneficioDTO(String nombre, String descripcion, String puntosNecesarios, String imagenPath, Boolean puedeCanjear, String id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosNecesarios = puntosNecesarios;
        this.imagenPath = imagenPath;
        this.puedeCanjear = puedeCanjear;
        this.id = id;
    }

    public Integer puntosNecesariosInteger() {
        return Integer.parseInt(this.puntosNecesarios);
    }

    public static BeneficioDTO obtenerBeneficioDTO(BeneficioOfrecido bo) {
        return new BeneficioDTO(
                bo.getNombre(),
                bo.getDescripcion(),
                bo.getPuntosNecesarios().toString(),
                bo.getImagenPath(),
                null,
                String.valueOf(bo.getId())
        );
    }

    public static BeneficioDTO obtenerBeneficioDTO(Context context) {
        return new BeneficioDTO(
                context.formParam("nombre"),
                context.formParam("rubro"),
                context.formParam("descripcion"),
                context.formParam("puntos"),
                null,
                context.formParam("beneficioId")
        );
    }
}
