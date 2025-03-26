package domain.heladera.sensores;

import config.ServiceLocator;
import domain.broker.receptores.ReceptorSenTemperatura;
import domain.heladera.Heladera;
import domain.heladera.sensores.datosRecibidos.MedicionTemperatura;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import services.sensores.SensorTemperaturaService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity @DiscriminatorValue("SENSOR_TEMPERATURA")
public class SensorTemperatura extends SensorPersistente{
    @Transient
    private ReceptorSenTemperatura receptorSenTemperatura;

    @Transient
    private MedicionTemperatura medicionTemperatura;

    @OneToMany(mappedBy = "sensorTemperatura")
    private List<MedicionTemperatura> ultimasTemperaturas = new ArrayList<>();

    public SensorTemperatura(Heladera heladera){
        this.setId(UUID.randomUUID().toString());
        this.setHeladera(heladera);
        this.setFechaDeColocacion(LocalDateTime.now());
        this.setReceptorSenTemperatura(new ReceptorSenTemperatura(this));
        recepcionDelDato();
    }

    public SensorTemperatura(Heladera heladera, LocalDateTime fecha){
        this.setId(UUID.randomUUID().toString());
        this.setHeladera(heladera);
        this.setFechaDeColocacion(fecha);
        this.setReceptorSenTemperatura(new ReceptorSenTemperatura(this));
        recepcionDelDato();
    }

    public SensorTemperatura(SensorPersistente sensor){
        this.setId(sensor.getId());
        this.setHeladera(sensor.getHeladera());
        this.setFechaDeColocacion(sensor.getFechaDeColocacion());
        this.setReceptorSenTemperatura(new ReceptorSenTemperatura(this));
        recepcionDelDato();
    }

    public void registrarMedicion(MedicionTemperatura medicionTemperatura){
        this.medicionTemperatura = medicionTemperatura;

        ultimasTemperaturas.add(medicionTemperatura);
    }

    public void recepcionDelDato(){
        this.receptorSenTemperatura.getClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Conexión perdida: " + cause.getMessage());
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String temperatura = new String(message.getPayload());
                Double temperaturaHeladera;

                try {
                    temperaturaHeladera = Double.parseDouble(temperatura);
                } catch (NumberFormatException e) {
                    System.err.println("Error al parsear la temperatura: " + temperatura);
                    return;
                }

                ServiceLocator.instanceOf(SensorTemperaturaService.class).setearMedicionActual(temperaturaHeladera, receptorSenTemperatura.getSensorTemperatura());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }});
    }
}
