package repositorios.colaboraciones;

import domain.colaboraciones.RegistroDePersonasVulnerables;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;
import java.util.Optional;

public class RepoRegistroPersonasVulnerables implements WithSimplePersistenceUnit {
    public Optional<RegistroDePersonasVulnerables> buscarPorIdVulnerable(String id) {
        try {
            RegistroDePersonasVulnerables registroDePersonasVulnerables = (RegistroDePersonasVulnerables) entityManager()
                    .createQuery("from " + RegistroDePersonasVulnerables.class.getName() + " where id =:id")
                    .setParameter("id", id)
                    .getSingleResult();

            return Optional.of(registroDePersonasVulnerables);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public List<RegistroDePersonasVulnerables> buscarTodas() {
        return entityManager()
                .createQuery("from " + RegistroDePersonasVulnerables.class.getName()).getResultList();
    }
}
