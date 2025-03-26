package repositorios.heladera;

import domain.colaboraciones.SolicitudApertura;
import domain.heladera.Heladera;
import domain.heladera.entradaSalida.EntradaSalida;
import domain.heladera.incidente.Incidente;
import domain.reportes.ReporteGeneral;
import domain.viandas.Vianda;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.time.LocalDate;
import java.util.List;

public class RepoMovimientos implements WithSimplePersistenceUnit {

    @SuppressWarnings("unchecked")
    public List<EntradaSalida> entradaSalidaList(Heladera heladera){
        return entityManager().
                createQuery("from " + EntradaSalida.class.getName() + " where heladera_id =:id_heladera").
                setParameter("id_heladera", heladera.getId()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<SolicitudApertura> solicitudAperturas(Heladera heladera){
        return entityManager().
                createQuery("from " + SolicitudApertura.class.getName() + " where heladera_id =:id_heladera").
                setParameter("id_heladera", heladera.getId()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Incidente> incidentes(Heladera heladera){
        return entityManager().
                createQuery("from " + Incidente.class.getName() + " where heladera_id =:id_heladera").
                setParameter("id_heladera", heladera.getId()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Vianda> viandas(Heladera heladera){
        return entityManager().
                createQuery("from " + Vianda.class.getName() + " where id_heladera =:id_heladera").
                setParameter("id_heladera", heladera.getId()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ReporteGeneral> reportes(){
        return entityManager().
                createQuery("from " + ReporteGeneral.class.getName()).
                getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<ReporteGeneral> reportes_entre(LocalDate fechaInicio, LocalDate fechaFin) {
        return entityManager()
                .createQuery("from " + ReporteGeneral.class.getName() + " where fecha_realizacion between :fechaI and :fechaF", ReporteGeneral.class)
                .setParameter("fechaI", fechaInicio)
                .setParameter("fechaF", fechaFin)
                .getResultList();
    }

}
