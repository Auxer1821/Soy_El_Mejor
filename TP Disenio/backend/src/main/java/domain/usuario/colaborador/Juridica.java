package domain.usuario.colaborador;

import domain.comunicaciones.Contacto;
import domain.forms.FormularioCompletado;
import domain.identificador.Rubro;
import domain.ubicaciones.Direccion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@DiscriminatorValue("JURIDICA")
public class Juridica extends UsuarioPersistente {
    @Column(name = "CUIT")
    private String cuit;

    @Column(name = "razon_social")
    private String razonSocial;

    @Column(name = "tipo")
    private String tipo;

    @Enumerated(EnumType.STRING)
    private Rubro rubro;

    @OneToMany(mappedBy = "juridica")
    private List<Contacto> mediosDeContactos;

    public Juridica(String razonSocial, String tipo, Rubro rubro, Direccion direccion, List<Contacto> mediosDeContactos, FormularioCompletado formularioCompletado) {
        setId(UUID.randomUUID().toString());
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro = rubro;
        setDireccion(direccion);
        this.setMediosDeContactos(mediosDeContactos);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.JURIDICA);
        this.mediosDeContactos = new ArrayList<>();
    }

    public Juridica(String CUIT, String razonSocial, String tipo, Rubro rubro, Direccion direccion, List<Contacto> mediosDeContactos, FormularioCompletado formularioCompletado) {
        setId(UUID.randomUUID().toString());
        this.cuit = CUIT;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro = rubro;
        setDireccion(direccion);
        this.setMediosDeContactos(mediosDeContactos);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.JURIDICA);
        this.mediosDeContactos = new ArrayList<>();
    }

    public Juridica(String username, String password, String email, String cuit, String razonSocial, String tipo, Rubro rubro, Direccion direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro = rubro;
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setDireccion(direccion);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.JURIDICA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(LocalDateTime.now());
    }

    public Juridica(String username, String password, String email, LocalDateTime fechaAlta, String cuit, String razonSocial, String tipo, Rubro rubro, Direccion direccion) {
        this.cuit = cuit;
        this.razonSocial = razonSocial;
        this.tipo = tipo;
        this.rubro = rubro;
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPassword(password);
        setEmail(email);
        setDireccion(direccion);
        setPuntos_adquiridos(0D);
        setRolUsuario(RolUsuario.JURIDICA);
        this.mediosDeContactos = new ArrayList<>();
        setFecha_alta(fechaAlta);
    }

    public void agregarContacto(Contacto contacto) {
        getMediosDeContactos().add(contacto);
    }
}
