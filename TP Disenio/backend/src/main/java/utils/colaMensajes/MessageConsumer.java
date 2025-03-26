package utils.colaMensajes;

import config.ServiceLocator;
import services.cargaMasiva.ColaboracionCSV;
import services.cargaMasiva.MigracionesService;

import static utils.csv.ReaderCSV.messageQueue;

public class MessageConsumer implements Runnable{
    @Override
    public void run() {
        while (true) {
            try {
                ColaboracionCSV colaboracion = messageQueue.take();

                ServiceLocator.instanceOf(MigracionesService.class).registrarColaboracionCSV(colaboracion);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
