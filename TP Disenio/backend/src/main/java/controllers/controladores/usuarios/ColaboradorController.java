package controllers.controladores.usuarios;


import config.ServiceLocator;
import controllers.DTOs.colaboradores.HumanaDTO;
import controllers.DTOs.colaboradores.JuridicaDTO;
import controllers.DTOs.colaboradores.LoginDTO;
import domain.password.InfoUsuario;
import domain.password.ValidadorDeContrasenia;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.UsuarioPersistente;
import exceptions.altaUsuarios.DatosDuplicadosException;
import exceptions.altaUsuarios.NotUserFoundException;
import io.javalin.http.Context;
import services.ColaboradorService;
import utils.passwords.PasswordUtils;

import java.util.*;

public class ColaboradorController {

    private ColaboradorService colaboradorService;

    public ColaboradorController(ColaboradorService colaboradorService) {
        this.colaboradorService = colaboradorService;
    }

    public void login(Context context) {
        Boolean valor = false;

        Map<String, Object> model = new HashMap<>();
        model.put("isLogged", valor);

        context.render("routes/homepage/login.hbs", model);
    }

    public void check_login(Context context) {
        LoginDTO loginDTO = new LoginDTO();

        loginDTO.setUsername(context.formParam("username"));
        loginDTO.setPassword(context.formParam("password"));

        try {
            UsuarioPersistente usuario = colaboradorService.verificarUsuario(loginDTO);

            context.sessionAttribute("id_usuario", usuario.getId());
            context.sessionAttribute("username", usuario.getUsername());
            context.sessionAttribute("rol", usuario.getRolUsuario().toString());
            context.sessionAttribute("email", usuario.getEmail());

            context.redirect("/inicio");
        } catch (NotUserFoundException e) {
            // Capturar el error y devolverlo al usuario
            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());

            context.render("routes/homepage/login.hbs", model);
        }
    }

    public void register(Context context) {
        context.render("routes/forms_usuarios/crear_cuenta.hbs");
    }

    public void crear_humana(Context context) {
        //Formulario formulario = bucarFormulario();

        //Map<String, Object> model = new HashMap<>();
        //model.put("formulario", formulario);

        context.render("routes/forms_usuarios/crear_humana.hbs");// agrega modal 
    }

    public void crear_humana_precargada(Context context) {
        Optional<Humana> humana = colaboradorService.obtenerColaboradorHumano(context.pathParam("id"));

        Map<String, Object> model = new HashMap<>();
        if (humana.isPresent()) {
            HumanaDTO humanaDTO = new HumanaDTO(
                    humana.get().getNombre(),
                    humana.get().getApellido(),
                    humana.get().getTipo_documento().toString(),
                    humana.get().getDni(),
                    humana.get().getEmail()
            );

            model.put("datos_usuario", humanaDTO);

            context.render("routes/forms_usuarios/crear_humana.hbs", model);
        } else {
            model.put("error", "El usuario no ha sido cargado");

            context.render("routes/forms_usuarios/crear_humana.hbs", model);
        }
    }

    public void guardar_humano_precargado(Context context) {
        HumanaDTO humanaDTO = null;
        try {
            humanaDTO = recepcionarHumanaDTO(context);

            colaboradorService.actualizarHumanoPrecargado(humanaDTO, context.pathParam("id"));

            context.redirect("/");
        } catch (Exception e) {
            // Capturar el error y devolverlo al usuario
            Map<String, Object> model = new HashMap<>();
            model.put("datos_usuario", humanaDTO);
            model.put("error", e.getMessage());

            // Renderizar la vista con el mensaje de error
            context.render("routes/forms_usuarios/crear_humana.hbs", model);
        }
    }

    public void guardar_humana(Context context) {
        HumanaDTO humanaDTO = null;

        //FormularioCompletado formularioCompletado = null;

        try {

            //formularioCompletado = recepcionarFormularioCompletado(context);

            humanaDTO = recepcionarHumanaDTO(context);

            //colaboradorService.darAltaFormularioCompletado(formularioCompletado);

            colaboradorService.darAltaHumana(humanaDTO);

            context.redirect("/");

        } catch (Exception e) {
            // Capturar el error y devolverlo al usuario
            Map<String, Object> model = new HashMap<>();
            model.put("datos_usuario", humanaDTO);
            model.put("error", e.getMessage());

            // Renderizar la vista con el mensaje de error
            context.render("routes/forms_usuarios/crear_humana.hbs", model);
        }
    }

    public void crear_juridica(Context context) {
        context.render("routes/forms_usuarios/crear_juridica.hbs");
    }

    public void guardar_juridica(Context context) {
        JuridicaDTO juridica = new JuridicaDTO();
        try {
            // Rellenar el DTO con los datos del formulario
            juridica.setRazonSocial(context.formParam("razon_social"));
            juridica.setRubro(context.formParam("rubro"));
            juridica.setTipoOrganizacion(context.formParam("tipo_organizacion"));
            juridica.setCuit(context.formParam("cuit"));
            juridica.setProvincia(context.formParam("provincia"));
            juridica.setLocalidad(context.formParam("nombreLocalidad"));
            juridica.setDireccion(context.formParam("direccion"));
            juridica.setTiposContacto(context.formParams("medios_contacto"));
            juridica.setDatosContacto(context.formParams("contactos"));
            juridica.setUsername(context.formParam("username"));

            juridica.setEmail(context.formParam("email"));

            List<String> contraseniaError = ServiceLocator.instanceOf(ValidadorDeContrasenia.class).mensajesDeError(new InfoUsuario(new ArrayList<>(), juridica.getUsername(), context.formParam("password")));

            if (contraseniaError.isEmpty()) {
                juridica.setPassword(PasswordUtils.hashPassword(context.formParam("password")));
                // Intentar registrar el usuario
                colaboradorService.darAltaJuridica(juridica);

                context.redirect("/");
            } else {
                Map<String, Object> model = new HashMap<>();
                model.put("datos_usuario", juridica);
                model.put("error", contraseniaError);

                context.render("routes/forms_usuarios/crear_juridica.hbs", model);
            }

        } catch (DatosDuplicadosException e) {
            // Capturar el error y devolverlo al usuario
            Map<String, Object> model = new HashMap<>();
            model.put("datos_usuario", juridica);
            model.put("error", "El usuario y/o email ingresados ya se encuentan asociados a otra cuenta.");

            // Renderizar la vista con el mensaje de error
            context.render("routes/forms_usuarios/crear_juridica.hbs", model);
        }
    }

    public HumanaDTO recepcionarHumanaDTO(Context context) {
        HumanaDTO humanaDTO = new HumanaDTO();

        humanaDTO.setNombre(context.formParam("fname"));
        humanaDTO.setApellido(context.formParam("lname"));
        humanaDTO.setTipoDocumento(context.formParam("tipo_documento"));
        humanaDTO.setNroDocumento(context.formParam("nro_documento"));
        humanaDTO.setProvincia(context.formParam("provincia"));
        humanaDTO.setLocalidad(context.formParam("nombreLocalidad"));
        humanaDTO.setDireccion(context.formParam("direccion"));
        humanaDTO.setDob(context.formParam("birthdate"));
        humanaDTO.setTiposContacto(context.formParams("medio_contacto"));
        humanaDTO.setDatosContacto(context.formParams("contacto"));
        humanaDTO.setUsername(context.formParam("username"));
        humanaDTO.setEmail(context.formParam("email"));

        List<String> contraseniaError = ServiceLocator.instanceOf(ValidadorDeContrasenia.class).mensajesDeError(new InfoUsuario(new ArrayList<>(), humanaDTO.getUsername(), context.formParam("password")));

        if (contraseniaError.isEmpty()) {
            humanaDTO.setPassword(PasswordUtils.hashPassword(context.formParam("password")));
        } else {
            StringBuilder errores = new StringBuilder();

            contraseniaError.forEach(error -> {
                errores.append(error).append("\n");
            });

            throw new RuntimeException(errores.toString());
        }

        return humanaDTO;
    }
}

