package repositorios.tarjetas;

import domain.tarjetas.Tarjeta;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;


public class RepoTarjetas implements WithSimplePersistenceUnit {

    @SuppressWarnings("unchecked")
    public Tarjeta buscarPorIdHumana(String id) {
        return (Tarjeta) entityManager().
                createQuery("from " + Tarjeta.class.getName() + " where id_humana =:id").
                setParameter("id", id).
                getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public Tarjeta buscarPorID(String id) {
        return (Tarjeta) entityManager().
                createQuery("from " + Tarjeta.class.getName() + " where codigo = :id").
                setParameter("id", id).
                getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public void guardar(Tarjeta tarjeta) {
        entityManager().persist(tarjeta);
    }

    @SuppressWarnings("unchecked")
    public List<Tarjeta> buscarTodas() {
        return entityManager()
                .createQuery("from " + Tarjeta.class.getName()).getResultList();
    }
}
