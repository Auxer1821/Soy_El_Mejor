package domain.password.condiciones_error;


import domain.password.InfoUsuario;
import org.eclipse.paho.client.mqttv3.MqttException;

public interface CondicionesErrorContrasenia {

    public abstract Boolean cumpleError(InfoUsuario infoUsuario);

    public abstract String mensajeDeError();
}
