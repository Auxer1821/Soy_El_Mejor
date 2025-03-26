package services;

import Converters.TipoDocumento.ConverterDocumento;
import controllers.DTOs.colaboradores.HumanaDTO;
import controllers.DTOs.colaboradores.JuridicaDTO;
import controllers.DTOs.colaboradores.LoginDTO;
import domain.comunicaciones.Contacto;
import domain.comunicaciones.TipoDeContacto;
import domain.identificador.Rubro;
import domain.ubicaciones.Direccion;
import domain.ubicaciones.Localidad;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.colaborador.UsuarioPersistente;
import exceptions.altaUsuarios.DatosDuplicadosException;
import exceptions.altaUsuarios.NotUserFoundException;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.colaboradores.RepoUsuario;
import repositorios.puntos.RepositorioDeCanjes;
import utils.passwords.PasswordUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ColaboradorService {
    private RepoUsuario repoUsuario;
    private RepoHumana repoHumana;
    private RepoJuridica repoJuridica;

    public ColaboradorService(RepoUsuario repoUsuario, RepoHumana repoHumana, RepoJuridica repoJuridica) {
        this.repoUsuario = repoUsuario;
        this.repoJuridica = repoJuridica;
        this.repoHumana = repoHumana;
    }

    public void darAltaHumana(HumanaDTO humanaDTO) {
        Humana nuevo_usuario = new Humana(
                humanaDTO.getUsername(),
                humanaDTO.getPassword(),
                humanaDTO.getEmail(),
                humanaDTO.getNroDocumento(),
                ConverterDocumento.convertirDocumento(humanaDTO.getTipoDocumento()),
                humanaDTO.getNombre(),
                humanaDTO.getApellido(),
                new Direccion(humanaDTO.getDireccion(), new Localidad(humanaDTO.getLocalidad()), humanaDTO.getProvincia()),
                LocalDate.parse(humanaDTO.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        generar_contactos(humanaDTO.getDatosContacto(), humanaDTO.getTiposContacto(), nuevo_usuario);

        try {
            repoUsuario.beginTransaction();
            repoUsuario.persist(nuevo_usuario);

            nuevo_usuario.getMediosDeContactos().forEach(contacto -> repoUsuario.persist(contacto));

            repoUsuario.commitTransaction();
        } catch (Exception e) {
            repoUsuario.rollbackTransaction();
            throw new DatosDuplicadosException("El usuario y/o email ingresados ya se encuentan asociados a otra cuenta.");
        }
    }

    public void darAltaJuridica(JuridicaDTO juridicaDTO) {
        Juridica nuevo_usuario = new Juridica(
                juridicaDTO.getUsername(),
                juridicaDTO.getPassword(),
                juridicaDTO.getEmail(),
                juridicaDTO.getCuit(),
                juridicaDTO.getRazonSocial(),
                juridicaDTO.getRubro(),
                Rubro.valueOf(juridicaDTO.getTipoOrganizacion().toUpperCase()),
                new Direccion(juridicaDTO.getDireccion(), new Localidad(juridicaDTO.getLocalidad()), juridicaDTO.getProvincia())
        );

        generar_contactos(juridicaDTO.getDatosContacto(), juridicaDTO.getTiposContacto(), nuevo_usuario);

        try {
            repoUsuario.beginTransaction();
            repoUsuario.persist(nuevo_usuario);

            nuevo_usuario.getMediosDeContactos().forEach(contacto -> repoUsuario.persist(contacto));

            repoUsuario.commitTransaction();
        } catch (Exception e) {
            repoUsuario.rollbackTransaction();
            throw new DatosDuplicadosException("El usuario y/o email ingresados ya se encuentan asociados a otra cuenta.");
        }
    }

    public void generar_contactos(List<String> datosContacto, List<String> tipoContacto, Humana humana) {
        for (int i = 0; i < datosContacto.size(); i++) {
            Contacto contacto = new Contacto(
                    TipoDeContacto.valueOf(tipoContacto.get(i).toUpperCase()),
                    datosContacto.get(i),
                    humana);

            humana.agregarContacto(contacto);
        }
    }

    public void generar_contactos(List<String> datosContacto, List<String> tipoContacto, Juridica juridica) {
        for (int i = 0; i < datosContacto.size(); i++) {
            Contacto contacto = new Contacto(
                    TipoDeContacto.valueOf(tipoContacto.get(i).toUpperCase()),
                    datosContacto.get(i),
                    juridica);

            juridica.agregarContacto(contacto);
        }
    }

    public UsuarioPersistente verificarUsuario(LoginDTO loginDTO) {
        try {
            // Intentamos buscar un usuario de tipo Humana
            UsuarioPersistente usuario = repoUsuario.buscarPorUsername(loginDTO.getUsername());

            if (PasswordUtils.checkPassword(loginDTO.getPassword(), usuario.getPassword())) {
                return usuario;
            } else {
                throw new NotUserFoundException("Usuario no encontrado");
            }
        } catch (Exception e) {
            // Manejar cualquier excepción inesperada que pueda surgir
            throw new NotUserFoundException("Usuario y/o contraseña incorrecta");
        }
    }

    public Optional<Humana> obtenerColaboradorHumano(String id) {
        return repoHumana.buscarHumanoPrecargado(id);
    }

    public UsuarioPersistente obtenerUsuario(String username) {
        return repoUsuario.buscarPorUsername(username);
    }

    public Juridica obtenerColaboradorJuridico(String id) {
        return repoJuridica.buscarPorID(id);
    }

    public void actualizarHumanoPrecargado(HumanaDTO humanaDTO, String id) {
        Humana humana = repoHumana.buscarPorID(id);

        generar_contactos(humanaDTO.getDatosContacto(), humanaDTO.getTiposContacto(), humana);

        repoHumana.beginTransaction();
        try {
            repoHumana.actualizarHumanoPrecargado(humana, humanaDTO);

            List<Contacto> contactosExistentes = repoHumana.buscarContactos(humana.getId());

            List<Contacto> nuevosContactos = humana.getMediosDeContactos();

            for (Contacto nuevoContacto : nuevosContactos) {
                boolean existe = contactosExistentes.stream()
                        .anyMatch(contactoExistente -> contactoExistente.getDatoContacto().equals(nuevoContacto.getDatoContacto()));

                if (!existe) {
                    repoUsuario.persist(nuevoContacto);
                }
            }
            repoHumana.commitTransaction();
        } catch (Exception e) {
            repoHumana.rollbackTransaction();
            throw new DatosDuplicadosException("El usuario y/o email ingresados ya se encuentran asociados a otra cuenta.");
        }
    }
}
