package utils.Varios;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathFotosBeneficios {
    private final static String fotos_beneficios = "src/main/resources/public/img/beneficios";

    public static Path getPath(String filename) {
        return Paths.get(fotos_beneficios).resolve(filename).toAbsolutePath();
    }
}
