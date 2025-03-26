package domain.tarjetas;

import domain.usuario.colaborador.Humana;
import domain.usuario.vulnerable.PersonaVulnerable;
import utils.PropertiesManager;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "tarjetas")
@NoArgsConstructor
@ToString
public class Tarjeta {
    @Id
    private String codigo;

    @OneToOne
    @JoinColumn(name = "id_vulnerable")
    private PersonaVulnerable personaVulnerable;

    @OneToOne
    @JoinColumn(name = "id_humana")
    private Humana personaHumana;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaDeCreacion;

    @OneToMany(mappedBy = "tarjeta_usada")
    private List<Usos> usos;

    private static PropertiesManager properties = new PropertiesManager("properties/tarjeta.properties");

    public Tarjeta(PersonaVulnerable personaVulnerable) {
        this.codigo = UUID.randomUUID().toString();
        this.personaVulnerable = personaVulnerable;
        this.fechaDeCreacion = LocalDateTime.now();
        this.usos = new ArrayList<Usos>();
    }

    public Tarjeta(Humana personaHumana) {
        this.codigo = UUID.randomUUID().toString();
        this.personaHumana = personaHumana;
        this.fechaDeCreacion = LocalDateTime.now();
        this.usos = new ArrayList<Usos>();
    }

    public Integer usosDiarios() {
        //TODO: Ponerlo en un propertie

        return this.personaVulnerable.cantidadDeMenoresACargo() * properties.getPropertyInteger("COEFICIENTE_MENORES") + properties.getPropertyInteger("CONSTANTE_BASE");
    }

    public Integer usosGastados(){
        return Math.toIntExact(usos.stream().filter(Usos::fueUsadaHoy).count());
    }

    public Integer usosDisponibles() {
        return this.usosDiarios() - this.usosGastados();
    }

    public Boolean puedeUsar(){
        return usosDisponibles() > 0;
    }

    public void aniadirUsos(Usos uso) { usos.add(uso);}


        public static void main(String args[]) {
            PersonaVulnerable hijo = new PersonaVulnerable("Fabian", "Hernandez", LocalDateTime.of(2014, 3, 4, 10, 20), LocalDateTime.of(2005, 3, 4, 10, 20), null, null);
            ArrayList<PersonaVulnerable> hijos = new ArrayList<PersonaVulnerable>();
            hijos.add(hijo);
            PersonaVulnerable padre = new PersonaVulnerable("Fabian", "Hernandez", LocalDateTime.of(1965, 3, 4, 10, 20), LocalDateTime.of(2005, 3, 4, 10, 20), null, hijos);

            Tarjeta tarjeta = new Tarjeta(padre);

            System.out.println("Usos disponibles: " + tarjeta.usosDisponibles());
        }
}

