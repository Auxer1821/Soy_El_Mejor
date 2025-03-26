package utils.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import config.ServiceLocator;
import services.cargaMasiva.ColaboracionCSV;
import services.cargaMasiva.MigracionesService;
import utils.colaMensajes.MessageConsumer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ReaderCSV {
    public static BlockingQueue<ColaboracionCSV> messageQueue = new LinkedBlockingQueue<>();

    public static void readColaboracionCSV(Path path) {
        new Thread(new MessageConsumer()).start();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(path, StandardOpenOption.READ), StandardCharsets.UTF_8))) {
            String[] row;
            csvReader.skip(1);
            while ((row = csvReader.readNext()) != null) {
                String tipo = row[0];
                String documento = row[1];
                String nombre = row[2];
                String apellido = row[3];
                String mail = row[4];
                LocalDate fechaColaboracion = LocalDate.parse(row[5], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String formaColaboracion = row[6];
                int cantidad = Integer.parseInt(row[7]);

                ColaboracionCSV colaboracion = new ColaboracionCSV(tipo, documento, nombre, apellido, mail,
                        fechaColaboracion, formaColaboracion, cantidad);

                messageQueue.put(colaboracion);
            }
        } catch (IOException | CsvValidationException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static void readColaboracionCSV(InputStream inputStream) {
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] nextLine;

            csvReader.readNext();

            // Leer el archivo línea por línea
            while ((nextLine = csvReader.readNext()) != null) {
                if (isValidRow(nextLine)) {
                    procesarFila(nextLine);
                } else {
                    System.err.println("Fila inválida: " + String.join(",", nextLine));
                }
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static void procesarFila(String[] fila) {
        // Aquí puedes manipular los datos de cada fila
        // Por ejemplo, insertar los datos en la base de datos
        System.out.println("Procesando fila: " + String.join(",", fila));
    }

    private static boolean isValidRow(String[] fila) {
        // Valida la fila (puedes agregar reglas según lo que esperas del CSV)
        return fila.length > 0 && fila[0] != null && !fila[0].isEmpty();  // Ejemplo: asegura que no esté vacía
    }
}