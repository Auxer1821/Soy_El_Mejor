package domain.broker.receptores;

import domain.heladera.sensores.SensorTemperatura;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import utils.PropertiesManager;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.UUID;

@Getter
@Setter
public class ReceptorSenTemperatura extends ReceptorPersistente {
    @OneToOne
    private SensorTemperatura sensorTemperatura;

    public ReceptorSenTemperatura(SensorTemperatura sensorTemperatura) {
        setId(UUID.randomUUID().toString());
        this.sensorTemperatura = sensorTemperatura;

        try {
            setClient(new MqttClient("tcp://broker.hivemq.com:1883",
                    sensorTemperatura.getId(), new MemoryPersistence()));

            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            connOpts.setConnectionTimeout(10);
            connOpts.setKeepAliveInterval(20);

            getClient().connect(connOpts);

            if (getClient().isConnected()) {
                getClient().subscribe("heladera/" + sensorTemperatura.getHeladera().getId() + "/sensores/temperaturas");
            } else {
                System.err.println("Cliente no está conectado, no se puede suscribir.");
            }

            MqttClient finalClient = (MqttClient) getClient();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (finalClient.isConnected()) {
                        System.out.println("Disconnecting...");
                        finalClient.disconnect();
                        System.out.println("Disconnected");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        } catch (MqttException e) {
            System.err.println("Error al conectar con el broker: " + e.getMessage());
            if (e.getReasonCode() == MqttException.REASON_CODE_FAILED_AUTHENTICATION) {
                System.err.println("Error: Fallo de autenticación. Verifica las credenciales.");
            } else if (e.getReasonCode() == MqttException.REASON_CODE_BROKER_UNAVAILABLE) {
                System.err.println("Error: El broker está inaccesible. Verifica la URL y el puerto.");
            } else {
                System.err.println("Error inesperado: " + e.getReasonCode());
            }
            e.printStackTrace();
            System.err.println(e.getReasonCode());
            throw new RuntimeException(e);
        }
    }
}
