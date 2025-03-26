package Converters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ToBase64 {
    public static String convert(InputStream inputStream){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            byte[] imageBytes = baos.toByteArray();

            // Convertir a Base64
            return Base64.getEncoder().encodeToString(imageBytes);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static String convert(String path) {
        try {
            Path imagePath = Path.of(path);
            byte[] imageBytes = Files.readAllBytes(imagePath);

            return Base64.getEncoder().encodeToString(imageBytes);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

}
