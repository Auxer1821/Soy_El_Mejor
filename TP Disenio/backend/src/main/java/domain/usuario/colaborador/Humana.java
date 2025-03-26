package domain.usuario.colaborador;

import domain.comunicaciones.Contacto;
import domain.forms.FormularioCompletado;
import domain.identificador.TipoDocumento;
import domain.ubicaciones.Direccion;
import domain.usuario.colaborador.RolUsuario;
import domain.usuario.colaborador.UsuarioPersistente;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString
@DiscriminatorValue("HUMANA")
public class Humana extends UsuarioPersistente {
    @Column(name = "nro_documento")
    private String dni;

    @Enumerated(EnumType.STRING)
    private TipoDocumento tipo_documento;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate dob;

    @OneToMany(mappedBy = "humana")
    private List<Contacto> mediosDeContactos = new ArrayList<>();

    public Humana(String nombre, String apellido, Direccion direccion, List<Contacto> mediosDeContacto, Double puntos) {
        setId(UUID.randomUUID().toString());
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        this.setMediosDeContactos(mediosDeContacto);
        setPuntos_adquiridos(puntos);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
    }


    public Humana(String nombre, String apellido, Direccion direccion, List<Contacto> mediosDeContacto, FormularioCompletado formularioCompletado) {
        setId(UUID.randomUUID().toString());
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        this.setMediosDeContactos(mediosDeContacto);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
    }

    public Humana(String DNI, TipoDocumento tipo_documento, String nombre, String apellido, Direccion direccion, List<Contacto> mediosDeContacto, FormularioCompletado formularioCompletado) {
        setId(UUID.randomUUID().toString());
        this.dni = DNI;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        this.setMediosDeContactos(mediosDeContacto);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
    }

    public Humana(String username, String password, String email, String dni, TipoDocumento tipo_documento, String nombre, String apellido, Direccion direccion) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.dni = dni;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(LocalDateTime.now());
    }

    public Humana(String username, String password, String email, String dni, TipoDocumento tipo_documento, String nombre, String apellido, Direccion direccion, Double puntos) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.dni = dni;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        setPuntos_adquiridos(puntos);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(LocalDateTime.now());
    }

    public Humana(String username, String password, String email, LocalDateTime fechaAlta, String dni, TipoDocumento tipo_documento, String nombre, String apellido, Direccion direccion) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.dni = dni;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(fechaAlta);
    }

    public Humana(String nombre, String apellido, String email, String dni, TipoDocumento tipo_documento) {
        setId(UUID.randomUUID().toString());
        setEmail(email);
        this.dni = dni;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        setFecha_alta(LocalDateTime.now());
    }

    public Humana(String username, String password, String email, String dni, TipoDocumento tipo_documento, String nombre, String apellido, Direccion direccion, LocalDate dob) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        this.dni = dni;
        this.tipo_documento = tipo_documento;
        this.nombre = nombre;
        this.apellido = apellido;
        setDireccion(direccion);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.HUMANA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(LocalDateTime.now());
    }

    public void agregarContacto(Contacto contacto) {
        getMediosDeContactos().add(contacto);
    }
}
