package services.tecnicos;

import controllers.DTOs.tecnicos.TecnicoDTO;
import domain.usuario.tecnico.Tecnico;
import exceptions.colaboraciones.BeneficioNotFoundException;
import repositorios.tecnico.RepoTecnicos;

import java.util.stream.Collectors;

import java.util.List;

public class TecnicosService {
    private RepoTecnicos repoTecnicos;

    public TecnicosService(RepoTecnicos repoTecnicos) {
        this.repoTecnicos = repoTecnicos;
    }

    public void crearTecnico(TecnicoDTO tecnico) {

    }

    public void modificarHabilitacionPorCuil(String cuil, Boolean habilitado) {
        repoTecnicos.modificarHabilitacionPorCuil(cuil, habilitado);
    }

    public List<TecnicoDTO> obtenerTodos() {
        try {
            List<Tecnico> tecnicos = repoTecnicos.buscarTodos();

            return tecnicos.stream().map(tecnico -> new TecnicoDTO(
                    tecnico.getCuil(),
                    tecnico.getNombre(),
                    tecnico.getApellido(),
                    tecnico.getDocumento() != null ? tecnico.getDocumento().getNumero() : "",
                    tecnico.getUbicacion() != null ? String.valueOf(tecnico.getUbicacion().getLatitud()) : "",
                    tecnico.getUbicacion() != null ? String.valueOf(tecnico.getUbicacion().getLongitud()) : "",
                    tecnico.getHabilitado()
            )).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
