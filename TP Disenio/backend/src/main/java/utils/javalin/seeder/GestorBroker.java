package utils.javalin.seeder;

import config.ServiceLocator;
import domain.heladera.Heladera;
import domain.heladera.sensores.SensorMovimiento;
import domain.heladera.sensores.SensorPersistente;
import domain.heladera.sensores.SensorTemperatura;
import domain.heladera.sensores.VerificadorLlegadaTarjeta;
import repositorios.RepoGenerico;
import services.heladera.HeladeraService;

import java.util.ArrayList;
import java.util.List;

public class GestorBroker {
    public List<SensorMovimiento> sensoresMovimientos;
    public List<SensorTemperatura> sensoresTemperaturas;
    public List<VerificadorLlegadaTarjeta> verificadoresLlegadaTarjetas;

    public GestorBroker() {
        this.sensoresMovimientos = new ArrayList<>();
        this.sensoresTemperaturas = new ArrayList<>();
        this.verificadoresLlegadaTarjetas = new ArrayList<>();
    }

    public void recuperar() {
        HeladeraService heladeraService = ServiceLocator.instanceOf(HeladeraService.class);

        List<Heladera> heladeras = heladeraService.buscarHeladerasActivas();

        heladeras.forEach(heladera -> {
            List<SensorPersistente> sensores = heladeraService.buscarSensoresHeladera(heladera);

            recuperarVerificador(heladera);
            recuperarSensores(sensores);
        });

        System.out.println("----- Todos los datos de las heladeras fueron recuperados -----");
    }


    private void recuperarSensores(List<SensorPersistente> sensores) {
        for (SensorPersistente sensor : sensores) {
            switch (sensor.getTipoSensor()) {
                case SENSOR_TEMPERATURA -> {
                    SensorTemperatura sensorT = new SensorTemperatura(sensor);
                    this.sensoresTemperaturas.add(sensorT);
                }
                case SENSOR_MOVIMIENTO -> {
                    SensorMovimiento sensorM = new SensorMovimiento(sensor);
                    this.sensoresMovimientos.add(sensorM);
                }
            }
        }
    }

    public void recuperarVerificador(Heladera heladera) {
        VerificadorLlegadaTarjeta verificadorLlegadaTarjeta = new VerificadorLlegadaTarjeta(heladera);
        this.verificadoresLlegadaTarjetas.add(verificadorLlegadaTarjeta);
    }

    public void registrar(Object objeto) {
        if (objeto instanceof VerificadorLlegadaTarjeta) {
            this.verificadoresLlegadaTarjetas.add((VerificadorLlegadaTarjeta) objeto);
        } else if (objeto instanceof SensorMovimiento) {
            this.sensoresMovimientos.add((SensorMovimiento) objeto);
            ServiceLocator.instanceOf(RepoGenerico.class).persist(objeto);
        } else if (objeto instanceof SensorTemperatura) {
            this.sensoresTemperaturas.add((SensorTemperatura) objeto);
            ServiceLocator.instanceOf(RepoGenerico.class).persist(objeto);
        }
    }
}
