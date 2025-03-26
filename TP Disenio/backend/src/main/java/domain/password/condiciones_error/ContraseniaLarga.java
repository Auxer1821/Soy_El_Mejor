package domain.password.condiciones_error;

import domain.password.InfoUsuario;

public class ContraseniaLarga implements CondicionesErrorContrasenia{

    private static int maximoCaracteres = 64;
    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario) {

        return infoUsuario.getContrasenia().length() > maximoCaracteres;
    }

    @Override
    public String mensajeDeError() {
        return "La contrasenia debe tener entre 8 y 64 caracteres";
    }
}
