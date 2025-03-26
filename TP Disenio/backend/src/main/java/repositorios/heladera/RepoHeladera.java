package repositorios.heladera;

import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.Modelo;
import domain.heladera.incidente.EstadoIncidente;
import domain.heladera.incidente.Incidente;
import domain.heladera.incidente.TipoIncidente;
import domain.heladera.sensores.SensorMovimiento;
import domain.heladera.sensores.SensorPersistente;
import domain.heladera.sensores.SensorTemperatura;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.springframework.ui.Model;

import javax.persistence.EntityTransaction;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class RepoHeladera implements WithSimplePersistenceUnit {

    public void guardar(Heladera heladera) {
        entityManager().persist(heladera);
    }

    public Heladera buscarPorId(String id) {
        return (Heladera) entityManager().
                createQuery("from " + Heladera.class.getName() + " where id = :id").
                setParameter("id", id).
                getSingleResult();
    }

    public Heladera buscarPorNombre(String nombre) {
        return (Heladera) entityManager().
                createQuery("from " + Heladera.class.getName() + " where nombre = :nombre").
                setParameter("nombre", nombre).
                getSingleResult();
    }

    @SuppressWarnings("unchecked")
    public List<Heladera> buscarTodas() {
        return entityManager().
                createQuery("from " + Heladera.class.getName()).
                getResultList();
    }

    public void eliminarHeladeraPorId(String id) {
        EntityTransaction transaction = entityManager().getTransaction();
        try {
            transaction.begin();

            entityManager().createQuery("DELETE FROM Vianda v WHERE v.heladera.id = :idHeladera")
                    .setParameter("idHeladera", id)
                    .executeUpdate();

            CriteriaBuilder cb = entityManager().getCriteriaBuilder();
            CriteriaDelete<Heladera> criteriaDelete = cb.createCriteriaDelete(Heladera.class);
            Root<Heladera> root = criteriaDelete.from(Heladera.class);

            criteriaDelete.where(cb.equal(root.get("id"), id));

            entityManager().createQuery(criteriaDelete).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }


    public List<Heladera> buscarHeladerasEnFuncionamientoParaDonar() {
        List<Heladera> hfuncionando = null;

        try {
            CriteriaBuilder cb = entityManager().getCriteriaBuilder();
            CriteriaQuery<Heladera> criteriaQuery = cb.createQuery(Heladera.class);
            Root<Heladera> root = criteriaQuery.from(Heladera.class);

            criteriaQuery.select(root)
                    .where(cb.equal(root.get("estado"), Estado.FUNCIONAMIENTO),
                            cb.lessThan(root.get("cantidadViandas"), root.get("maxCantidadViandas")));

            hfuncionando = entityManager().createQuery(criteriaQuery).getResultList();
        } finally {
            entityManager().close();
        }

        return hfuncionando;
    }

    public List<Heladera> buscarHeladerasActivas() {
        List<Heladera> hActivas = null;

        try {
            CriteriaBuilder cb = entityManager().getCriteriaBuilder();
            CriteriaQuery<Heladera> criteriaQuery = cb.createQuery(Heladera.class);
            Root<Heladera> root = criteriaQuery.from(Heladera.class);

            // Modificar la condición para incluir registros donde cantidadViandas es null
            criteriaQuery.select(root)
                    .where(cb.notEqual(root.get("estado"), Estado.INACTIVA));

            hActivas = entityManager().createQuery(criteriaQuery).getResultList();
        } finally {
            entityManager().close();
        }

        return hActivas;
    }

    @SuppressWarnings("unchecked")
    public List<SensorPersistente> buscarSensoresHeladera(String id) {
        return entityManager().
                createQuery("from " + SensorPersistente.class.getName() + " where heladera_id =:id").
                setParameter("id", id).
                getResultList();
    }

    public SensorTemperatura buscarSensorTemperatura(String id) {
        return (SensorTemperatura) entityManager().
                createQuery("from " + SensorTemperatura.class.getName() + " where heladera_id =:id").
                setParameter("id", id).
                getSingleResult();
    }

    public SensorMovimiento buscarSensorMovimiento(String id) {
        return (SensorMovimiento) entityManager().
                createQuery("from " + SensorMovimiento.class.getName() + " where heladera_id =:id").
                setParameter("id", id).
                getSingleResult();
    }

    @Transactional
    public void modificarEstado(String id, Estado estado) {
        Heladera heladera = entityManager().find(Heladera.class, id);
        heladera.setEstado(estado);
    }

    @Transactional
    public void modificarTemperatura(String id, Double temp) {
        Heladera heladera = entityManager().find(Heladera.class, id);
        heladera.setTemperaturaActual(temp);
    }

    @Transactional
    public void modificarTemperaturaYEstado(String id, Double temp, Estado estado) {
        Heladera heladera = entityManager().find(Heladera.class, id);
        heladera.setEstado(estado);
        heladera.setTemperaturaActual(temp);
    }

    public void editarHeladeraPorId(Heladera datosHeladera) {
        EntityTransaction transaction = entityManager().getTransaction();

        try {
            transaction.begin();

            Heladera heladera = entityManager().find(Heladera.class, datosHeladera.getId());

            if (heladera != null) {
                heladera.setNombre(datosHeladera.getNombre());
                heladera.setEstado(datosHeladera.getEstado());
                heladera.setDireccion(datosHeladera.getDireccion());

                entityManager().merge(heladera);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Modelo> obtenerModelos() {
        return entityManager().
                createQuery("from " + Modelo.class.getName()).
                getResultList();
    }

    public Modelo obtenerModelo(Long idModelo) {
        return (Modelo) entityManager().
                createQuery("from " + Modelo.class.getName() + " where id = :id_modelo").
                setParameter("id_modelo", idModelo).
                getSingleResult();
    }

    public List<Incidente> getIncidentesUltSemana() {
        return entityManager()
                .createQuery("FROM " + Incidente.class.getName() + " i WHERE i.fecha_incidente BETWEEN :startDate AND :endDate", Incidente.class)
                .setParameter("startDate", LocalDateTime.now().minusDays(6).with(LocalTime.MIN))
                .setParameter("endDate", LocalDateTime.now().with(LocalTime.MAX))
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Incidente> getTodosLosIncidentes() {
        return entityManager()
                .createQuery("FROM " + Incidente.class.getName())
                .getResultList();
    }

    public Incidente buscarIncidenteTecnico(String cuil) {
        return (Incidente) entityManager().
                createQuery("from " + Incidente.class.getName() + " where tecn_asignado = :cuil and estado = :estado").
                setParameter("cuil", cuil).
                setParameter("estado", EstadoIncidente.NO_SOLUCIONADO).
                getSingleResult();
    }

}