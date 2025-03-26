package services.puntos;

import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.sistemaPuntuacion.Canje;
import exceptions.colaboraciones.BeneficioNotFoundException;
import repositorios.RepositorioDeBeneficios;
import repositorios.puntos.RepositorioDeCanjes;

import java.util.List;

public class PuntosService {

    private RepositorioDeCanjes repositorioDeCanjes;

    public PuntosService(RepositorioDeCanjes repositorioDeCanjes) {
        this.repositorioDeCanjes = repositorioDeCanjes;
    }

    public List<Canje> buscarTodos(String id) {
        try{
            List<Canje> canjes = repositorioDeCanjes.buscarTodos(id);

            return canjes;
        }catch (Exception e){
            throw new BeneficioNotFoundException("Error al cargar los canjes. Por favor intentelo mas tarde.");
        }
    }

    public List<Canje> buscarUltimos10Canjes(String id) {
        try{
            List<Canje> canjes = repositorioDeCanjes.buscarUltimos10Canjes(id);

            return canjes;
        }catch (Exception e){
            throw new BeneficioNotFoundException("Error al cargar los canjes. Por favor intentelo mas tarde.");
        }
    }

}
