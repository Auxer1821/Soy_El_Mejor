package CronTasks;

import domain.broker.transmisores.Asunto;
import domain.broker.transmisores.TransmisorMQTT;
import domain.heladera.Heladera;
import domain.tarjetas.Tarjeta;
import domain.usuario.colaborador.Humana;
import org.eclipse.paho.client.mqttv3.MqttException;
import repositorios.colaboradores.RepoHumana;
import repositorios.heladera.RepoHeladera;
import repositorios.tarjetas.RepoTarjetas;

import java.net.UnknownHostException;

public class LlegadaTarjeta {
    public static void main(String[] args) throws UnknownHostException, MqttException {
        RepoHumana repoHumana = new RepoHumana();
        RepoTarjetas repoTarjetas = new RepoTarjetas();
        RepoHeladera repoHeladera = new RepoHeladera();

        Humana humana = repoHumana.buscarPorUsername("luiscastro");
        Tarjeta tarjeta = repoTarjetas.buscarPorIdHumana(humana.getId());
        Heladera heladera = repoHeladera.buscarPorNombre("Heladera UTN - CABA");

        TransmisorMQTT transmisorMQTT = new TransmisorMQTT();

        transmisorMQTT.enviarMensaje(heladera.getId(), tarjeta.getCodigo(), Asunto.APERTURA);
    }
}
