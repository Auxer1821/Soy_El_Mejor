package services.donaciones;

import controllers.DTOs.colaboradores.UsuarioDTO;
import controllers.DTOs.donaciones.BeneficioDTO;
import domain.colaboraciones.beneficios.BeneficioOfrecido;
import domain.sistemaPuntuacion.Canje;
import domain.usuario.colaborador.Humana;
import domain.usuario.colaborador.Juridica;
import domain.usuario.colaborador.RolUsuario;
import domain.usuario.colaborador.UsuarioPersistente;
import exceptions.colaboraciones.BeneficioNotFoundException;
import repositorios.RepoGenerico;
import repositorios.RepositorioDeBeneficios;
import repositorios.colaboradores.RepoHumana;
import repositorios.colaboradores.RepoJuridica;
import repositorios.puntos.RepositorioDeCanjes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BeneficiosService {
    private RepositorioDeBeneficios repositorioDeBeneficios;
    private RepoGenerico repoGenerico;
    private RepositorioDeCanjes repoCanje;
    private RepoJuridica repoJuridica;
    private RepoHumana repoHumana;

    public BeneficiosService(RepositorioDeBeneficios repositorioDeBeneficios, RepoGenerico repoGenerico, RepositorioDeCanjes repoCanje, RepoJuridica repoJuridica, RepoHumana repoHumana) {
        this.repositorioDeBeneficios = repositorioDeBeneficios;
        this.repoGenerico = repoGenerico;
        this.repoCanje = repoCanje;
        this.repoJuridica = repoJuridica;
        this.repoHumana = repoHumana;
    }

    public void CanjearBeneficio(UsuarioPersistente usuario, BeneficioDTO beneficioDTO) throws Exception {
        BeneficioOfrecido beneficio = repositorioDeBeneficios.buscarPorID(Long.valueOf(beneficioDTO.getId()));
        Canje canje;



        if (usuario.getPuntos_adquiridos() <= beneficio.getPuntosNecesarios()) {
            throw new Exception("Puntos insuficientes");
        }

        if(usuario.getRolUsuario() == RolUsuario.JURIDICA) {

            Juridica juridica = repoJuridica.buscarPorID(usuario.getId());
            canje = new Canje(LocalDate.now(), beneficio, null, juridica);
        }
        else {
            Humana humana = repoHumana.buscarPorID(usuario.getId());
            canje = new Canje(LocalDate.now(), beneficio, humana, null);
        }



        System.out.println(usuario.getPuntos_adquiridos());

        usuario.setPuntos_adquiridos(usuario.getPuntos_adquiridos() - beneficio.getPuntosNecesarios());

        System.out.println(usuario.getPuntos_adquiridos());

        repoGenerico.guardar(canje);
        repoGenerico.guardar(usuario);

    }

    public List<BeneficioOfrecido> buscarTodas() {
        try {
            List<BeneficioOfrecido> bos = repositorioDeBeneficios.buscarTodas();

            return bos;
        } catch (Exception e) {
            throw new BeneficioNotFoundException("Error al cargar beneficios. Por favor intentelo mas tarde.");
        }
    }

    public List<BeneficioOfrecido> buscarPorNombre(String nombre) {
        try {
            List<BeneficioOfrecido> bos = repositorioDeBeneficios.buscarPorNombre(nombre);

            return bos;
        } catch (Exception e) {
            throw new BeneficioNotFoundException("El beneficio solicitado no existe");
        }
    }
}
