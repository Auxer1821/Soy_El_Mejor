package utils.Varios;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GestorDeArchivo {

    private Map<String, String> archivosGuardados;

    // Constructor que recibe la ruta del archivo
    public GestorDeArchivo() {
        this.archivosGuardados = new HashMap<>();
    }

    // Método para leer el contenido del archivo y guardarlo en una cadena
    public String leerArchivo(String path) {
        if (archivosGuardados.containsKey(path)) {
            return archivosGuardados.get(path);
        } else {
            StringBuilder contenido = new StringBuilder();

            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            archivosGuardados.put(path, contenido.toString());
            return contenido.toString();
        }
    }

    // Método para verificar si una palabra está presente en el contenido del archivo
    public boolean contienePalabra(String contrasenia) {
        String archivo = this.leerArchivo("public/documents/vulnerable_passwords.txt");
        return archivo.contains(contrasenia);
    }

    // Método para escribir en el archivo
    public void escribirEnArchivo(String filePath, String texto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(texto);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
