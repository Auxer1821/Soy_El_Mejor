package services.cargaMasiva;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Getter
public class ColaboracionCSV {
    private String tipo;
    private String documento;
    private String nombre;
    private String apellido;
    private String mail;
    private LocalDate fechaColaboracion;
    private String formaColaboracion;
    private Integer cantidad;

    public ColaboracionCSV(String tipo, String documento, String nombre, String apellido, String mail, LocalDate fechaColaboracion, String formaColaboracion, Integer cantidad) {
        this.tipo = tipo;
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.fechaColaboracion = fechaColaboracion;
        this.formaColaboracion = formaColaboracion;
        this.cantidad = cantidad;
    }
}
