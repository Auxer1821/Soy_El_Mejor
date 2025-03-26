package services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import controllers.DTOs.otros.ComunidadDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class RecomComunidadService {
    public List<ComunidadDTO> solicitarComunidades(Double latitud, Double longitud, Integer radio) {
        List<ComunidadDTO> comunidadDTOList = new ArrayList<>();

        try {
            HttpClient client = HttpClient.newHttpClient();

            String url_api = "http://localhost:5000/recomendar";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url_api + "?lat=" + latitud + "&lon=" + longitud + "&radio=" + radio))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gsonMapper = new Gson();

                // Obtenemos el objeto con las comunidades
                JsonObject jsonObject = gsonMapper.fromJson(response.body(), JsonObject.class);

                // Accedemos al array "Comunidades cerca de tu locacion"
                JsonArray comunidadesArray = jsonObject.getAsJsonArray("Comunidades cerca de tu locacion");

                // Recorremos el array
                for (JsonElement comunidadElement : comunidadesArray) {
                    JsonArray comunidadInfo = comunidadElement.getAsJsonArray();

                    String nombre = comunidadInfo.get(0).getAsString();
                    String direccion = comunidadInfo.get(1).getAsString();

                    ComunidadDTO comunidad = new ComunidadDTO(nombre, direccion);

                    comunidadDTOList.add(comunidad);
                }

                return comunidadDTOList;
            } else {
                throw new RuntimeException("Error: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        RecomComunidadService recomendadorAdapter = new RecomComunidadService();

        System.out.println(recomendadorAdapter.solicitarComunidades(-34.6037,-58.3816,5));
    }
}


