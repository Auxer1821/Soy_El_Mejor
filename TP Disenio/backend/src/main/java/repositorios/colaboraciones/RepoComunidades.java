package repositorios.colaboraciones;

import domain.ubicaciones.comunidades.Comunidad;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.NoResultException;
import java.util.Optional;

public class RepoComunidades implements WithSimplePersistenceUnit {
    public Optional<Comunidad> buscarPorNombreYDireccion(String nombre, String direccion) {
        try {
            Comunidad comunidad = (Comunidad) entityManager()
                    .createQuery("from " + Comunidad.class.getName() + " where nombre =:nombre and direccion =:direccion")
                    .setParameter("nombre", nombre)
                    .setParameter("direccion", direccion)
                    .getSingleResult();
            return Optional.of(comunidad);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
