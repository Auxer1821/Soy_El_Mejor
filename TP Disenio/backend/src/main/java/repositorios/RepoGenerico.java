package repositorios;


import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;

public class RepoGenerico implements WithSimplePersistenceUnit {
    @SuppressWarnings("unchecked")
    public List<Object> buscarTodas(Object o) {
        return entityManager().
                createQuery("from " + o.getClass().getName()).
                getResultList();
    }

    public void guardar(Object o){
        beginTransaction();
        persist(o);
        commitTransaction();
    }
}
