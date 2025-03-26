package domain.password.condiciones_error;

import config.ServiceLocator;
import domain.password.InfoUsuario;
import org.eclipse.paho.client.mqttv3.MqttException;
import utils.Varios.GestorDeArchivo;

public class ContraseniasVulnerable implements CondicionesErrorContrasenia{
    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario){
        return ServiceLocator.instanceOf(GestorDeArchivo.class).contienePalabra(infoUsuario.getContrasenia());
    }

    @Override
    public String mensajeDeError() {
        return "La contrasenia es muy comun";
    }


}
