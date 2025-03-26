package exceptions.altaUsuarios;


import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatosDuplicadosException extends RuntimeException {
    public DatosDuplicadosException(SQLIntegrityConstraintViolationException e) {
        super(extraerNombreConstraint(e));
    }

    public DatosDuplicadosException(String message) {
        super(message);
    }

    public static String extraerNombreConstraint(SQLIntegrityConstraintViolationException e) {
        String mensaje = e.getMessage();

        System.out.println(mensaje);

        // Utiliza expresión regular para encontrar el nombre de la restricción
        String regex = "Violation of UNIQUE KEY constraint '(.*?)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mensaje);

        if (matcher.find()) {
            return matcher.group(1); // Retorna el nombre de la restricción
        }

        return "No se encontró información sobre la restricción.";
    }
}
