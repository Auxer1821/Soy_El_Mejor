package services.notificadores;

import domain.comunicaciones.Mensaje;
import domain.comunicaciones.Notificador;
import domain.heladera.Heladera;
import domain.usuario.tecnico.Tecnico;
import domain.usuario.tecnico.buscadorTecnicoCercano.BuscadorTecnicoCercano;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import repositorios.tecnico.RepoTecnicos;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
public class NotificadorDeTecnicos {
    private RepoTecnicos repoTecnicos;

    public Optional<Tecnico> notificarTecnicoMasCercano(Heladera heladera){
        List<Tecnico> tecnicos = repoTecnicos.buscarTodos();

        if(!tecnicos.isEmpty()){
            Optional<Tecnico> tecnico = BuscadorTecnicoCercano.buscarTecnicoCercano(tecnicos, heladera.getDireccion().getCoordenadas());

            tecnico.ifPresent(
                    value -> {
                        Notificador.notificar(new Mensaje("Fijate maestro que la heladera ubicada en " + heladera.getDireccion().getDireccion() + " esta funcionando medio mal. Pegate una vuelta si podes dale." + "\n" + "Gracias mostro"
                                        , "TEMPERATURA FUERA DE LIMITES")
                                , value.getContacto());


                    });

            return tecnico;
        }
        return null;
    }
}
