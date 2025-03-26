package repositorios.tarjetas;

import domain.colaboraciones.SolicitudApertura;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class RepoSolicitudesApertura implements WithSimplePersistenceUnit {

    @SuppressWarnings("unchecked")
    public List<SolicitudApertura> obtenerSolicitudes(String c_tarjeta, String id_h) {
        return entityManager().createQuery("from " + SolicitudApertura.class.getName() +
                " where codigo_tarjeta = :codigo and heladera_id = :id ").setParameter("codigo", c_tarjeta)
                .setParameter("id", id_h).getResultList();
    }

}
