package domain.broker.transmisores;

import lombok.Getter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import utils.PropertiesManager;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Getter
public class TransmisorMQTT {
    private String brokerUrl;
    private String clienteID;
    private MqttClient client;

    public TransmisorMQTT() throws MqttException, UnknownHostException {
        this.brokerUrl = "tcp://broker.hivemq.com:1883";
        this.clienteID = InetAddress.getLocalHost().toString();
        this.client = new MqttClient(brokerUrl, clienteID, new MemoryPersistence());

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        this.client.connect(connOpts);
    }

    public void enviarMensaje(String heladeraId, String mensaje, Asunto asunto) throws MqttException {
        String topic;
        switch (asunto) {
            case TEMPERATURA -> topic = "heladera/" + heladeraId + "/sensores/temperaturas";
            case ALERTA -> topic = "heladera/" + heladeraId + "/sensores/alertas/fraude";
            case APERTURA -> topic = "heladera/" + heladeraId + "/apertura_entrante";
            default -> throw new RuntimeException("No existe el topic");
        }

        MqttMessage message = new MqttMessage(mensaje.getBytes());
        message.setQos(2);
        client.publish(topic, message);
    }
}
