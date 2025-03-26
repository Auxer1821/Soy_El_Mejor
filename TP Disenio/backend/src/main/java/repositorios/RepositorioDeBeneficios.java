package repositorios;

import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.usuario.colaborador.Humana;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;

public class RepositorioDeBeneficios implements WithSimplePersistenceUnit {
    @SuppressWarnings("unchecked")
    public List<BeneficioOfrecido> buscarTodas() {
        return entityManager().
                createQuery("from " + BeneficioOfrecido.class.getName()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<BeneficioOfrecido> buscarPorNombre(String nombre_beneficio) {
        return entityManager().
                createQuery("from " + BeneficioOfrecido.class.getName() + " where nombre = :nombre").
                setParameter("nombre", nombre_beneficio).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public BeneficioOfrecido buscarPorID(Long id) {
        return (BeneficioOfrecido) entityManager().
                createQuery("from " + BeneficioOfrecido.class.getName() + " where id = :id").
                setParameter("id", id).getSingleResult();
    }
}
