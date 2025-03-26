package utils.passwords;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // Método para encriptar la contraseña
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Método para verificar la contraseña
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        String password = "miContraseñaSegura";
        String hashedPassword = hashPassword(password);
        System.out.println("Contraseña encriptada: " + hashedPassword);

        // Verificación
        boolean isMatch = checkPassword(password, hashedPassword);
        System.out.println("¿La contraseña coincide? " + isMatch);
    }
}