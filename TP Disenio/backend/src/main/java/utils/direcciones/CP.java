package utils.direcciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class CP{

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?q=";

    public static String obtenerCodigoPostal(String direccion, String ciudad) {
        try {
            // Construir la URL para la solicitud HTTP
            String query = direccion + ", " + ciudad;
            String urlStr = NOMINATIM_URL + query.replace(" ", "%20") + "&format=json&addressdetails=1";

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Leer la respuesta
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parsear el JSON usando json-simple
            JSONParser parser = new JSONParser();
            JSONArray results = (JSONArray) parser.parse(response.toString());

            if (!results.isEmpty()) {
                JSONObject result = (JSONObject) results.get(0);
                JSONObject address = (JSONObject) result.get("address");

                if (address.containsKey("postcode")) {
                    return (String) address.get("postcode");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String direccion = "Cornelio Saavedra 1100";
        String ciudad = "Muniz";
        String codigoPostal = obtenerCodigoPostal(direccion, ciudad);

        if (codigoPostal != null) {
            System.out.println("Código postal: " + codigoPostal);
        } else {
            System.out.println("No se encontró el código postal.");
        }
    }
}
