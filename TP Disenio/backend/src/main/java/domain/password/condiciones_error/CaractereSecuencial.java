package domain.password.condiciones_error;

import domain.password.InfoUsuario;

public class CaractereSecuencial implements CondicionesErrorContrasenia{
    @Override
    public Boolean cumpleError(InfoUsuario infoUsuario) {
        String contrasenia = infoUsuario.getContrasenia();
        boolean r = false;
        for (int i=0; i < (contrasenia.length() - 2) && (!r) ; i++){
            r= this.sonSecuenciales(contrasenia.charAt(i), contrasenia.charAt(i+1), contrasenia.charAt(i+2) );
        }
        return r;
    }

    private boolean sonSecuenciales(char primera, char segunda, char tercera) {
        return ( primera == (segunda -1) ) && ( segunda == (tercera - 1) );
    }

    @Override
    public String mensajeDeError() {
        return "La contrasenia tiene secuencias ej:123 , abc";
    }
}
