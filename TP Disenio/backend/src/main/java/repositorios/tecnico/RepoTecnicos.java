package repositorios.tecnico;

import domain.usuario.colaborador.Humana;
import domain.usuario.tecnico.Tecnico;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import utils.telegramSender.TelegramSender;

import javax.persistence.EntityTransaction;
import java.util.List;

public class RepoTecnicos implements WithSimplePersistenceUnit {

    @SuppressWarnings("unchecked")
    public Tecnico buscarPorCUIL(String cuil) {
        try {
            return (Tecnico) entityManager()
                    .createQuery("from " + Tecnico.class.getName() + " where cuil = :cuil")
                    .setParameter("cuil", cuil)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Tecnico> buscarTodos() {
        return entityManager()
                .createQuery("from " + Tecnico.class.getName() + " t")
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public boolean modificarHabilitacionPorCuil(String cuil, Boolean habilitado) {
        EntityTransaction transaction = entityManager().getTransaction();
        try {
            transaction.begin();

            Tecnico tecnico = entityManager().find(Tecnico.class, cuil);
            if (tecnico != null) {
                tecnico.setHabilitado(habilitado);
                entityManager().merge(tecnico);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
