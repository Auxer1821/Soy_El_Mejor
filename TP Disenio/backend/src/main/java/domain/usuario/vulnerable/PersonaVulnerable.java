package domain.usuario.vulnerable;

import domain.ubicaciones.Direccion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "personas_vulnerables")
@NoArgsConstructor
public class PersonaVulnerable {
    @Id
    private String id;

    @Column(name = "fname")
    private String nombre;

    @Column(name = "lname")
    private String apellido;

    @Column(name = "dob")
    private LocalDateTime fechaDeNacimiento;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaRegistro;

    @Embedded
    private Direccion direccion;

    @OneToMany
    @JoinColumn(name = "id_padre")
    private List<PersonaVulnerable> hijos;

    /*public PersonaVulnerable(String nombre, String apellido, LocalDateTime fechaDeNacimiento, LocalDateTime fechaRegistro, Direccion direccion, List<PersonaVulnerable> hijos) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.fechaRegistro = fechaRegistro;
        this.direccion = direccion;
        if(hijos == null) this.hijos = new ArrayList<PersonaVulnerable>(); else this.hijos = hijos;
    }*/
    public PersonaVulnerable(String nombre, String apellido, LocalDateTime fechaDeNacimiento, LocalDateTime fechaRegistro, Direccion direccion, List<PersonaVulnerable> hijos) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío.");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede ser nulo o vacío.");
        }
        if (fechaDeNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser nula.");
        }
        if (fechaRegistro == null) {
            throw new IllegalArgumentException("La fecha de registro no puede ser nula.");
        }
        if (direccion == null) {
            throw new IllegalArgumentException("La dirección no puede ser nula.");
        }

        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaDeNacimiento = fechaDeNacimiento;
        this.fechaRegistro = fechaRegistro;
        this.direccion = direccion;
        this.hijos = (hijos != null) ? hijos : new ArrayList<>();
    }


    public Boolean estaEnSituacionDeCalle(){
        return this.direccion == null;
    }

    public void agregarHijo(PersonaVulnerable hijo){
        this.hijos.add(hijo);
    }

    public Integer edad() {
        LocalDateTime hoy = LocalDateTime.now();
        long yearsBetween = ChronoUnit.YEARS.between(this.fechaDeNacimiento, hoy);
        return (int) yearsBetween;
    }

    public Boolean esMayorDeEdad(){
        return 18 > this.edad();
    }

    public Integer cantidadDeMenoresACargo(){
        return (int) this.hijos.stream().filter(hijo -> hijo.esMayorDeEdad()).count();
    }
}
