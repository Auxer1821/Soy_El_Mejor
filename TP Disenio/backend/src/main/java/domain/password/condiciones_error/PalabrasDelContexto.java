package domain.password.condiciones_error;

import domain.password.InfoUsuario;

public class PalabrasDelContexto implements CondicionesErrorContrasenia{
    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario) {
        boolean containsUsuario = infoUsuario.getContrasenia().toUpperCase().contains(infoUsuario.getUsuario().toUpperCase());
        boolean containsContextItem = false;
        for (String item : infoUsuario.getContexto()) {
            if (infoUsuario.getContrasenia().toUpperCase().contains(item.toUpperCase())) {
                containsContextItem = true;
                break;
            }
        }

        return containsUsuario || containsContextItem;
    }

    @Override
    public String mensajeDeError() {
        return "Tu nombre de usuario esta en la contraseña";
    }
}
