package repositorios.contactos;

import domain.comunicaciones.Contacto;
import domain.comunicaciones.TipoDeContacto;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;


public class RepoContactos implements WithSimplePersistenceUnit {
    @SuppressWarnings("unchecked")
    public Contacto buscarPorContactoYTipo(String contacto, TipoDeContacto tipo) {
        return (Contacto) entityManager().
                createQuery("from " + Contacto.class.getName() + " where tipo = :tipo and dato_contacto = :contacto").
                setParameter("tipo", tipo).
                setParameter("contacto", contacto)
                .getSingleResult();
    }
}



