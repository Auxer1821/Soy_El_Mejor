package controllers.DTOs.otros;

import domain.sistemaPuntuacion.Canje;
import lombok.Data;

@Data
public class CanjeDTO {
    private Long id;
    private Long id_beneficio;
    private String nombre_producto;
    private String id_humana;
    private String id_juridica;
    private String puntos_producto;
    private String fecha;

    public CanjeDTO(Long id, Long id_beneficio, String id_humana, String id_juridica, String fecha) {
        this.id = id;
        this.id_beneficio = id_beneficio;
        this.id_humana = id_humana;
        this.id_juridica = id_juridica;
        this.fecha = fecha;
    }

    public CanjeDTO(Long id, Long id_beneficio, String id_humana, String id_juridica, String fecha,
            String nombre_producto, String puntos_producto) {
        this.id = id;
        this.id_beneficio = id_beneficio;
        this.id_humana = id_humana;
        this.id_juridica = id_juridica;
        this.fecha = fecha;
        this.nombre_producto = nombre_producto;
        this.puntos_producto = puntos_producto;
    }

    public static CanjeDTO obtenerCanjeDTO(Canje canje) {
        String idHumana = (canje.getColaboradorHumana() != null) ? canje.getColaboradorHumana().getId() : null;
        String idJuridica = (canje.getColaboradorJuridica() != null) ? canje.getColaboradorJuridica().getId() : null;

        CanjeDTO canjeDTO = new CanjeDTO(
                canje.getId(),
                canje.getBeneficioCanjeado().getId(),
                idHumana,
                idJuridica,
                canje.getFechaDeCanje().toString(),
                canje.getBeneficioCanjeado().getNombre().toString(),
                canje.getBeneficioCosto().toString());
        return canjeDTO;
    }

}
