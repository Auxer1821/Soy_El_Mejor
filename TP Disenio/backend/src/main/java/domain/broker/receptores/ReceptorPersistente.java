package domain.broker.receptores;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.IMqttClient;

@Data
public abstract class ReceptorPersistente {
    private String id;
    private IMqttClient client;
}
