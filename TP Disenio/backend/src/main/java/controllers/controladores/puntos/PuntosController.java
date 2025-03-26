package controllers.controladores.puntos;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.donaciones.BeneficioDTO;
import controllers.DTOs.otros.CanjeDTO;
import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.sistemaPuntuacion.Canje;
import domain.usuario.colaborador.UsuarioPersistente;
import io.javalin.http.Context;
import repositorios.puntos.RepositorioDeCanjes;
import services.ColaboradorService;
import services.donaciones.BeneficiosService;
import services.puntos.PuntosService;
import utils.javalin.ICrudViewsHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PuntosController implements ICrudViewsHandler {
    private PuntosService puntosService;
    private BeneficiosService beneficiosService;
    private ColaboradorService colaboradorService;

    public PuntosController(PuntosService puntosService, BeneficiosService beneficiosService,
            ColaboradorService colaboradorService) {
        this.puntosService = puntosService;
        this.beneficiosService = beneficiosService;
        this.colaboradorService = colaboradorService;
    }

    // TODO: Cambiar misPuntos a misCanjes
    public void misPuntos(Context context) {
        UsuarioDTO usuarioDTO = UsuarioDTO.obtenerInfoUsuario(context);

        UsuarioPersistente usuarioPersistente = colaboradorService.obtenerUsuario(usuarioDTO.getUsername());

        usuarioDTO.actualizar(usuarioPersistente);

        List<Canje> canjes = puntosService.buscarUltimos10Canjes(usuarioDTO.getId());
        List<CanjeDTO> canjesAMostrar = new ArrayList<>();

        canjes.forEach(canje -> {
            CanjeDTO canjeDTO = CanjeDTO.obtenerCanjeDTO(canje);

            canjesAMostrar.add(canjeDTO);
        });
        Map<String, Object> model = new HashMap<>();
        model.put("user", usuarioDTO);
        model.put("canjes", canjesAMostrar);
        model.put("titulo", "Canjes realizados en el mes anterior");

        context.render("routes/puntos/mis_puntos.hbs", model);
    }

    @Override
    public void index(Context context) {

    }

    @Override
    public void show(Context context) {

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
