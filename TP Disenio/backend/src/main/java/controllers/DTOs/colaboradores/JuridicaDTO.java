package controllers.DTOs.colaboradores;

import lombok.Data;

import java.util.List;

@Data
public class JuridicaDTO {
    private String razonSocial;
    private String rubro;
    private String cuit;
    private String tipoOrganizacion;
    private String provincia;
    private String localidad;
    private String direccion;
    private List<String> tiposContacto;
    private List<String> datosContacto;
    private String username;
    private String password;
    private String email;
}
