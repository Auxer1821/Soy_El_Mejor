package domain.heladera.factory;

import config.ServiceLocator;
import domain.heladera.Estado;
import domain.heladera.Heladera;
import domain.heladera.Modelo;
import domain.heladera.sensores.SensorMovimiento;
import domain.heladera.sensores.SensorTemperatura;
import domain.heladera.sensores.VerificadorLlegadaTarjeta;
import domain.ubicaciones.Direccion;
import domain.viandas.Vianda;
import org.eclipse.paho.client.mqttv3.MqttException;
import utils.javalin.seeder.GestorBroker;

import java.time.LocalDateTime;
import java.util.*;

public class HeladeraFactory {
    public static Heladera create_order(Direccion direccion, String name) {
        Heladera heladera = new Heladera(direccion, name);
        heladera.setEstado(Estado.INACTIVA);
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name) {
        return new Heladera(direccion, name);
    }

    public static Heladera create(Direccion direccion, String name, Modelo model) {
        Heladera heladera = create(direccion, name);
        heladera.setModelo(model);
        heladera.setMaxCantidadViandas(model.getMaxCantidadViandas());
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Integer cantidadViandas) {
        Heladera heladera = create(direccion, name, model);
        heladera.setCantidadViandas(cantidadViandas);
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Estado state, LocalDateTime dateOfWorking) {
        Heladera heladera = create(direccion, name, model);
        heladera.setEstado(state);
        heladera.setFechaFuncionamiento(dateOfWorking);
        heladera.setViandas(new ArrayList<Vianda>());
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Estado state, LocalDateTime dateOfWorking, List<Vianda> viandas) {
        Heladera heladera = create(direccion, name, model, state, dateOfWorking);
        heladera.setViandas(viandas);
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Estado state, LocalDateTime dateOfWorking, List<Vianda> viandas, Integer diasFuncionamiento) {
        Heladera heladera = create(direccion, name, model, state, dateOfWorking, viandas);
        heladera.setDiasFuncionamiento(diasFuncionamiento);
        heladera.setCantidadViandas(viandas.size());
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Estado state, LocalDateTime dateOfWorking, List<Vianda> viandas, Double temperaturaActual) {
        Heladera heladera = create(direccion, name, model, state, dateOfWorking, viandas);
        heladera.setTemperaturaActual(temperaturaActual);
        return heladera;
    }

    public static Heladera create(Direccion direccion, String name, Modelo model, Estado state, LocalDateTime dateOfWorking, List<Vianda> viandas, Integer diasFuncionamiento, Double temperaturaActual) {
        Heladera heladera = create(direccion, name, model, state, dateOfWorking, viandas, diasFuncionamiento);
        heladera.setTemperaturaActual(temperaturaActual);
        return heladera;
    }

    public static void registrarHeladera(Heladera heladera) {
        try {
            SensorTemperatura sensorTemperatura = new SensorTemperatura(heladera);
            SensorMovimiento sensorMovimiento = new SensorMovimiento(heladera);
            VerificadorLlegadaTarjeta verificadorLlegadaTarjeta = new VerificadorLlegadaTarjeta(heladera);

            heladera.agregarSensor(sensorMovimiento);
            heladera.agregarSensor(sensorTemperatura);

            ServiceLocator.instanceOf(GestorBroker.class).registrar(sensorMovimiento);
            ServiceLocator.instanceOf(GestorBroker.class).registrar(sensorTemperatura);
            ServiceLocator.instanceOf(GestorBroker.class).registrar(verificadorLlegadaTarjeta);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
