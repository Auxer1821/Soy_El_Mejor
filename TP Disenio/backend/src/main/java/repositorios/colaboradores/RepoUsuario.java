package repositorios.colaboradores;

import domain.usuario.colaborador.UsuarioPersistente;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class RepoUsuario implements WithSimplePersistenceUnit {

    public UsuarioPersistente buscarPorUsername(String username) {
        return (UsuarioPersistente) entityManager().
                createQuery("from " + UsuarioPersistente.class.getName() + " where username = :username").
                setParameter("username", username).getSingleResult();
    }
}
