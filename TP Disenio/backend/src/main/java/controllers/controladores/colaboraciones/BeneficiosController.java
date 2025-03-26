package controllers.controladores.colaboraciones;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.donaciones.BeneficioDTO;
import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.colaborador.UsuarioPersistente;
import exceptions.colaboraciones.BeneficioNotFoundException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import services.ColaboradorService;
import services.donaciones.BeneficiosService;
import utils.javalin.ICrudViewsHandler;

import java.util.*;

public class BeneficiosController implements ICrudViewsHandler {
    private BeneficiosService beneficiosService;
    private ColaboradorService colaboradorService;

    public BeneficiosController(BeneficiosService beneficiosService, ColaboradorService colaboradorService) {
        this.beneficiosService = beneficiosService;
        this.colaboradorService = colaboradorService;
    }

    @Override
    public void index(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        try {
            UsuarioPersistente usuarioPersistente = colaboradorService.obtenerUsuario(usuarioDTO.getUsername());

            List<BeneficioOfrecido> beneficiosOfrecidos = beneficiosService.buscarTodas();
            List<BeneficioDTO> beneficiosAMostrar = new ArrayList<>();

            beneficiosOfrecidos.forEach(beneficioOfrecido -> {
                BeneficioDTO beneficioDTO2 = BeneficioDTO.obtenerBeneficioDTO(beneficioOfrecido);

                beneficioDTO2.setPuedeCanjear(
                        beneficioOfrecido.getPuntosNecesarios() <= usuarioPersistente.getPuntos_adquiridos());
                beneficiosAMostrar.add(beneficioDTO2);
            });

            usuarioDTO.actualizar(usuarioPersistente);

            Map<String, Object> model = new HashMap<>();
            model.put("beneficios", beneficiosAMostrar);
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);

        } catch (BeneficioNotFoundException e) {
            context.status(HttpStatus.NOT_FOUND);

            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);
        }
    }

    @Override
    public void show(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        try {
            UsuarioPersistente usuarioPersistente = colaboradorService.obtenerUsuario(usuarioDTO.getUsername());

            List<BeneficioOfrecido> beneficiosOfrecidos = beneficiosService.buscarTodas();
            List<BeneficioDTO> beneficiosAMostrar = new ArrayList<>();

            beneficiosOfrecidos.forEach(beneficioOfrecido -> {
                BeneficioDTO beneficioDTO2 = BeneficioDTO.obtenerBeneficioDTO(beneficioOfrecido);

                beneficioDTO2.setPuedeCanjear(
                        beneficioOfrecido.getPuntosNecesarios() <= usuarioPersistente.getPuntos_adquiridos());
                beneficiosAMostrar.add(beneficioDTO2);
            });

            usuarioDTO.actualizar(usuarioPersistente);

            Map<String, Object> model = new HashMap<>();
            model.put("beneficios", beneficiosAMostrar);
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);

        } catch (BeneficioNotFoundException e) {
            context.status(HttpStatus.NOT_FOUND);

            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);
        }
    }

    public void canjearBeneficio(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);
        try {

            UsuarioPersistente usuarioPersistente = colaboradorService.obtenerUsuario(usuarioDTO.getUsername());
            BeneficioDTO beneficioDTO = BeneficioDTO.obtenerBeneficioDTO(context);

            System.out.println(usuarioDTO.getPuntosAdquiridos());
            System.out.println(usuarioPersistente.getPuntos_adquiridos());

            beneficiosService.CanjearBeneficio(usuarioPersistente, beneficioDTO);

            System.out.println(usuarioPersistente.getPuntos_adquiridos());

            List<BeneficioOfrecido> beneficiosOfrecidos = beneficiosService.buscarTodas();
            List<BeneficioDTO> beneficiosAMostrar = new ArrayList<>();

            beneficiosOfrecidos.forEach(beneficioOfrecido -> {
                BeneficioDTO beneficioDTO2 = BeneficioDTO.obtenerBeneficioDTO(beneficioOfrecido);

                beneficioDTO2.setPuedeCanjear(
                        beneficioOfrecido.getPuntosNecesarios() <= usuarioPersistente.getPuntos_adquiridos());
                beneficiosAMostrar.add(beneficioDTO2);
            });

            usuarioDTO.actualizar(usuarioPersistente);

            Map<String, Object> model = new HashMap<>();
            model.put("beneficios", beneficiosAMostrar);
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);

        } catch (Exception e) {
            context.status(HttpStatus.CONFLICT);

            e.printStackTrace();

            Map<String, Object> model = new HashMap<>();
            model.put("error", e.getMessage());
            model.put("user", usuarioDTO);

            context.render("routes/beneficios/beneficioOfrecido.hbs", model);
        }

    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {

    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {

    }
}
