package CronTasks;

import domain.broker.transmisores.Asunto;
import domain.broker.transmisores.TransmisorMQTT;
import domain.heladera.Heladera;
import domain.heladera.sensores.SensorTemperatura;
import org.eclipse.paho.client.mqttv3.MqttException;
import repositorios.heladera.RepoHeladera;
import utils.Varios.NumeroRandom;

import java.net.UnknownHostException;
import java.util.List;

public class EnvioTemperaturas {
    public static void main(String[] args) throws UnknownHostException, MqttException {
        NumeroRandom generadorNumerosRandom = new NumeroRandom();
        RepoHeladera repoHeladera = new RepoHeladera();

        TransmisorMQTT transmisorTemperaturaMQTT = new TransmisorMQTT();

        Heladera heladera = repoHeladera.buscarPorNombre("Heladera UTN - CABA");

        // heladeras.forEach(heladera -> {
        SensorTemperatura sensorTemperatura = repoHeladera.buscarSensorTemperatura(heladera.getId());
        int temperaturaHeladera = 1000;

        try {
            transmisorTemperaturaMQTT.enviarMensaje(String.valueOf(sensorTemperatura.getHeladera().getId()),
                    String.valueOf(temperaturaHeladera), Asunto.TEMPERATURA);
            System.out.println("Se envio la temperatura satisfactoriamente");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        // });

    }
}
