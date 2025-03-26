package domain.heladera.sensores;

import config.ServiceLocator;
import domain.broker.receptores.ReceptorSenMovimiento;
import domain.broker.receptores.ReceptorSenTemperatura;
import domain.heladera.Heladera;
import domain.heladera.sensores.datosRecibidos.AlertaMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import services.sensores.SensorMovimientoService;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity @DiscriminatorValue("SENSOR_MOVIMIENTO")
public class SensorMovimiento extends SensorPersistente{
    @Transient
    private ReceptorSenMovimiento receptorSenMovimiento;

    @OneToMany(mappedBy = "sensorMovimiento")
    private List<AlertaMovimiento> alertas = new ArrayList<>();

    public SensorMovimiento(Heladera heladera){
        this.setId(UUID.randomUUID().toString());
        this.setHeladera(heladera);
        this.setFechaDeColocacion(LocalDateTime.now());
        this.setReceptorSenMovimiento(new ReceptorSenMovimiento(this));
        recepcionDelDato();
    }

    public SensorMovimiento(Heladera heladera, LocalDateTime fechaDeColocacion){
        this.setId(UUID.randomUUID().toString());
        this.setHeladera(heladera);
        this.setFechaDeColocacion(fechaDeColocacion);
        this.setReceptorSenMovimiento(new ReceptorSenMovimiento(this));
        recepcionDelDato();
    }

    public SensorMovimiento(SensorPersistente sensor){
        this.setId(sensor.getId());
        this.setHeladera(sensor.getHeladera());
        this.setFechaDeColocacion(sensor.getFechaDeColocacion());
        this.setReceptorSenMovimiento(new ReceptorSenMovimiento(this));
        recepcionDelDato();
    }

    public void registrarAlerta(AlertaMovimiento alertaMovimiento){
        this.alertas.add(alertaMovimiento);
    }

    public void recepcionDelDato(){
        this.getReceptorSenMovimiento().getClient().setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Conexión perdida: " + cause.getMessage());
                cause.printStackTrace();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String alerta = new String(message.getPayload());
                ServiceLocator.instanceOf(SensorMovimientoService.class).registrarAlertaMovimiento(alerta, receptorSenMovimiento.getSensorMovimiento());
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }
}
