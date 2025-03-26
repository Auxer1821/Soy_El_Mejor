package domain.password.condiciones_error;

import domain.password.InfoUsuario;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CarecteresRepetidos implements CondicionesErrorContrasenia{

    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario) {

        String expresion = "(.)\\1{2}";

        // Compilar la expresión regular en un objeto Pattern
        Pattern pattern = Pattern.compile(expresion);

        // Crear un objeto Matcher para la cadena de entrada
        Matcher matcher = pattern.matcher(infoUsuario.getContrasenia().toUpperCase());

        return matcher.find();
    }

    @Override
    public String mensajeDeError() {
        return "Tiene caracteres repetidos ej: aaa";
    }
}
