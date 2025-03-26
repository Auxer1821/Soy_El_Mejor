package repositorios.puntos;

import domain.sistemaPuntuacion.Canje;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class RepositorioDeCanjes implements WithSimplePersistenceUnit {

    public List<Canje> buscarTodos(String id) {
        return entityManager().
                createQuery("from " + Canje.class.getName() + " c where c.humana_id = :id or c.juridica_id = :id", Canje.class).
                setParameter("id", id).
                getResultList();
    }

    /*public List<Canje> buscarUltimos10Canjes(String id) {
        List<Canje> canjes = new ArrayList<>();

        try {
            CriteriaBuilder cb = entityManager().getCriteriaBuilder();
            CriteriaQuery<Canje> criteriaQuery = cb.createQuery(Canje.class);
            Root<Canje> root = criteriaQuery.from(Canje.class);

            // Aplicar la condición 'or' entre humana_id y juridica_id
            criteriaQuery.select(root)
                    .where(cb.or(
                            cb.equal(root.get("humana_id"), id),
                            cb.equal(root.get("juridica_id"), id)
                    ))
                    .orderBy(cb.desc(root.get("fecha_canje")));

            canjes = entityManager().createQuery(criteriaQuery)
                    .setMaxResults(10)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return canjes;
    }*/

    public List<Canje> buscarUltimos10Canjes(String id){
        List<Canje> canjes = new ArrayList<>();
        try {
            canjes = entityManager().createQuery(
                            "SELECT c FROM Canje c WHERE c.colaboradorHumana.id = :id OR c.colaboradorJuridica.id = :id ORDER BY c.fechaDeCanje DESC", Canje.class)
                    .setParameter("id", id)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return canjes;
    }


}
