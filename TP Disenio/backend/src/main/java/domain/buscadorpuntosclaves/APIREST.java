package domain.buscadorpuntosclaves;
import java.io.IOException;
import java.net.URL;

import lombok.Getter;
import lombok.Setter;

import javax.net.ssl.HttpsURLConnection;
import java.util.Scanner;

@Getter
@Setter
public class APIREST {
    private String url;

    public APIREST(String url) {
        this.url = url;
    }

    public Boolean GETRequest() throws IOException {
        URL urlObj = new URL(this.url);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode =connection.getResponseCode();
        System.out.println("Response code: " + responseCode);

        if(responseCode == HttpsURLConnection.HTTP_OK) {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()) {
                sb.append(scanner.nextLine());
            }
            System.out.println(sb);
            return true;
        } else {
            System.out.println("Error");
            return false;
        }
    }
}
