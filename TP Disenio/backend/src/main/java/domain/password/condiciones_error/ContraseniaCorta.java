package domain.password.condiciones_error;

import domain.password.InfoUsuario;

public class ContraseniaCorta implements CondicionesErrorContrasenia{

    private static int minimoCaracteres = 8;
    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario) {
        return infoUsuario.getContrasenia().length() < minimoCaracteres;
    }

    @Override
    public String mensajeDeError() {
        return "La contrasenia debe tener entre 8 y 64 caracteres";
    }
}
