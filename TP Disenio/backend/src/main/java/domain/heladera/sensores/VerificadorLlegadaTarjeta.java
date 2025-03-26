package domain.heladera.sensores;

import config.ServiceLocator;
import domain.broker.receptores.ReceptorTarjetaHeladera;
import domain.colaboraciones.SolicitudApertura;
import domain.heladera.AperturaFehaciente;
import domain.heladera.Heladera;
import domain.tarjetas.Tarjeta;
import domain.tarjetas.Usos;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import repositorios.RepoGenerico;
import repositorios.tarjetas.RepoSolicitudesApertura;
import repositorios.tarjetas.RepoTarjetas;
import services.Mqtt.MqttService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class VerificadorLlegadaTarjeta {
    private Heladera heladera;
    private ReceptorTarjetaHeladera receptor;

    public VerificadorLlegadaTarjeta(Heladera heladera){
        this.heladera = heladera;
        this.receptor = new ReceptorTarjetaHeladera(this);

        recepcionDelDato();
    }

    public void recepcionDelDato(){
        this.receptor.getClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Conexión perdida: " + cause.getMessage());
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String codigo_tarjeta = new String(message.getPayload());

                ServiceLocator.instanceOf(MqttService.class).puedeAbrir(heladera.getId(), codigo_tarjeta);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }});
    }
}
