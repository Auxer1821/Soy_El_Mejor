package controllers.DTOs.tecnicos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TecnicoDTO {
    private String cuil;
    private String nombre;
    private String apellido;
    private String documento;
    private Boolean habilitado;
    private String tipo;
    private String latitud;
    private String longitud;

    public TecnicoDTO(String cuil, String nombre, String apellido, String documento, String latitud, String longitud, Boolean habilitado) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.latitud = latitud;
        this.longitud = longitud;
        this.habilitado = habilitado;
    }
}
