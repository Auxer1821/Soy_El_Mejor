package repositorios.colaboradores;

import domain.usuario.colaborador.Juridica;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.*;

public class RepoJuridica implements WithSimplePersistenceUnit {

    public Juridica buscarPorID(String id){
        return (Juridica) entityManager().
                createQuery("from " + Juridica.class.getName() + " where id = :id").
                setParameter("id", id).
                getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public Juridica buscarPorUsername(String username) {
        return (Juridica) entityManager().
                createQuery("from " + Juridica.class.getName() + " where username = :username").
                setParameter("username", username).getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Juridica> buscarTodas() {
        return entityManager().
                createQuery("from " + Juridica.class.getName()).
                getResultList();
    }

    public Juridica buscarPrimero() {
        return (Juridica) entityManager()
                .createQuery("from " + Juridica.class.getName()).setMaxResults(1).getResultList().get(0);
    }
}
