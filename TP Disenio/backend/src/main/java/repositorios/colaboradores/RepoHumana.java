package repositorios.colaboradores;

import Converters.TipoDocumento.ConverterDocumento;
import controllers.DTOs.colaboradores.HumanaDTO;
import domain.comunicaciones.Contacto;
import domain.heladera.Heladera;
import domain.ubicaciones.Direccion;
import domain.ubicaciones.Localidad;
import domain.usuario.colaborador.Humana;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RepoHumana implements WithSimplePersistenceUnit {
    @SuppressWarnings("unchecked")
    public Humana buscarPorID(String id){
        return (Humana) entityManager().
                createQuery("from " + Humana.class.getName() + " where id = :id").
                setParameter("id", id).getSingleResult();
    }

    public Optional<Humana> buscarHumanoPrecargado(String id){
        try {
            Humana humana = (Humana)
                entityManager().
                createQuery("from " + Humana.class.getName() + " where id = :id").
                setParameter("id", id).getSingleResult();
            return Optional.of(humana);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Humana buscarPorUsername(String username){
        return (Humana) entityManager().
                createQuery("from " + Humana.class.getName() + " where username = :username").
                setParameter("username", username).getSingleResult();
    }


    @SuppressWarnings("unchecked")
    public List<Humana> buscarTodas() {
        return entityManager().
                createQuery("from " + Humana.class.getName()).
                getResultList();
    }

    public Humana buscarPrimero() {
        return (Humana) entityManager()
                .createQuery("from " + Humana.class.getName()).setMaxResults(1).getResultList().get(0);
    }

    public Optional<Humana> buscarColaborador(String nombre, String apellido, String documento, String email) {
        try {
            Humana Humana = (Humana) entityManager()
                    .createQuery("from " + Humana.class.getName() + " where nombre =:nombre and apellido =:apellido and nro_documento =: documento and email =: email")
                    .setParameter("nombre", nombre)
                    .setParameter("apellido", apellido)
                    .setParameter("documento", documento)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(Humana);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Transactional
    public void actualizarHumanoPrecargado(Humana humana, HumanaDTO humanaDTO) {
        humana.setDireccion(new Direccion(humanaDTO.getDireccion(), new Localidad(humanaDTO.getLocalidad()), humanaDTO.getProvincia()));
        humana.setDob(LocalDate.parse(humanaDTO.getDob(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        humana.setUsername(humanaDTO.getUsername());
        humana.setPassword(humanaDTO.getPassword());
        humana.setEmail(humanaDTO.getEmail());
        humana.setDob(LocalDate.parse(humanaDTO.getDob(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        humana.setNombre(humanaDTO.getNombre());
        humana.setApellido(humanaDTO.getApellido());
        humana.setTipo_documento(ConverterDocumento.convertirDocumento(humanaDTO.getTipoDocumento()));
        humana.setDni(humanaDTO.getNroDocumento());
    }

    @SuppressWarnings("unchecked")
    public List<Contacto> buscarContactos(String id) {
        return entityManager()
                .createQuery("from " + Humana.class.getName() + " where id = :id")
                .setParameter("id", id)
                .getResultList();
    }

    @Transactional
    public void actualizarPuntos(String id, Double puntos) {
        Humana humana = entityManager().find(Humana.class, id);
        humana.setPuntos_adquiridos(humana.getPuntos_adquiridos() + puntos);
    }
}
