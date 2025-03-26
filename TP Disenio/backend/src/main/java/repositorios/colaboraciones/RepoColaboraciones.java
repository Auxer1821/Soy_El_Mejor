package repositorios.colaboraciones;

import domain.colaboraciones.DistribucionViandas;
import domain.colaboraciones.DonacionDinero;
import domain.colaboraciones.DonacionVianda;
import domain.colaboraciones.EstadoDonacion;
import domain.usuario.colaborador.Humana;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RepoColaboraciones implements WithSimplePersistenceUnit {

    public Optional<DistribucionViandas> buscarPorId(Long id) {
        try {
            DistribucionViandas distribucionViandas = (DistribucionViandas) entityManager()
                    .createQuery("from " + DistribucionViandas.class.getName() + " where id =:id")
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(distribucionViandas);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void asignarColaborador(Long id, Humana humana) {
        DistribucionViandas distribucionViandas = entityManager().find(DistribucionViandas.class, id);
        distribucionViandas.setHumana(humana);
        distribucionViandas.setEstado(EstadoDonacion.ACEPTADA);
    }

    @Transactional
    public void modificarDistribucion(Long id) {
        DistribucionViandas distribucionViandas = entityManager().find(DistribucionViandas.class, id);
        distribucionViandas.setEstado(EstadoDonacion.REALIZADA);
        distribucionViandas.setFechaDeRealizacion(LocalDateTime.now());
    }

    @SuppressWarnings("unchecked")
    public List<DonacionDinero> buscarDNDeUnaHumana(String id) {
        return entityManager()
                .createQuery("from " + DonacionDinero.class.getName() + " where colaborador_humano_id =: id")
                .setParameter("id", id)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<DonacionDinero> buscarDNDeUnaJuridica(String id) {
        return entityManager()
                .createQuery("from " + DonacionDinero.class.getName() + " where colaborador_juridico_id =: id")
                .setParameter("id", id)
                .getResultList();
    }


    @SuppressWarnings("unchecked")
    public List<DonacionDinero> buscarTodas() {
        return entityManager()
                .createQuery("from " + DonacionDinero.class.getName()).getResultList();
    }

    public Optional<DonacionVianda> findDVPorId(Long id) {
        try {
            DonacionVianda donacionVianda = (DonacionVianda) entityManager()
                    .createQuery("from " + DonacionVianda.class.getName() + " where id =:id")
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(donacionVianda);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void modificarDonacionVianda(Long id) {
        DonacionVianda donacionVianda = entityManager().find(DonacionVianda.class, id);
        donacionVianda.setEstadoDonacion(EstadoDonacion.REALIZADA);
        donacionVianda.setFechaDeRealizacion(LocalDateTime.now());
    }
}
