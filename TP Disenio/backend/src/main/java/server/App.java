package server;


import org.eclipse.paho.client.mqttv3.MqttException;

public class App {


    public static void main(String[] args) throws MqttException {
        Server.init();
    }
}
