package repositorios.visitas;

import domain.usuario.tecnico.Visita;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;

public class RepoVisitas implements WithSimplePersistenceUnit {

    @SuppressWarnings("unchecked")
    public List<Visita> buscarVisitasPorIncidenteId(Long incidenteId) {
        return entityManager().createQuery(
                        "FROM " + Visita.class.getName() + " WHERE incidente_id = :incidenteId")
                .setParameter("incidenteId", incidenteId)
                .getResultList();
    }
}