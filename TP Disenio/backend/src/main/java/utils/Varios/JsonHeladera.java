/*package domain.utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import domain.heladera.Heladera;

public class JsonHeladera {
    private static String path = "../shared/heladeras.json";

    public void agregarHeladeraAJSON(Heladera heladera){
        JSONParser parser = new JSONParser();
        try {
            // Paso 1: Leer el archivo JSON como un JSONArray
            Object obj = parser.parse(new FileReader(path));
            JSONArray jsonArray = (JSONArray) obj;

            if( this.contieneId(heladera.getId().getId(), jsonArray) ) return;

            // Paso 2: Crear un nuevo objeto JSON para la nueva heladera
            JSONObject nuevaHeladera = new JSONObject();
            nuevaHeladera.put("id", heladera.getId());
            nuevaHeladera.put("name", heladera.getNombre());
            nuevaHeladera.put("lat", heladera.getCoordenadas().getLatitude());
            nuevaHeladera.put("lon", heladera.getCoordenadas().getLongitude());
            nuevaHeladera.put("icono", "heladeraEstandar");

            // Paso 3: Agregar el nuevo objeto JSON al JSONArray
            jsonArray.add(nuevaHeladera);

            // Paso 4: Escribir el JSONArray actualizado de nuevo en el archivo JSON
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(jsonArray.toJSONString());
            fileWriter.close();

            System.out.println("Heladera agregada correctamente al JSON.");


        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

    }

    private Boolean contieneId(String id ,JSONArray jsonArray){

        Boolean r = false;

        for (Object obj : jsonArray) {

            JSONObject jsonObject = (JSONObject) obj;
            String idUsuario = (String) jsonObject.get("id");

            if (idUsuario == id) {
                r = true;
                break; // No es necesario seguir buscando
            }

        }

        return r;
    }


}
*/

