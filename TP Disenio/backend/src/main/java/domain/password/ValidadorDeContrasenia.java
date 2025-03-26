package domain.password;


import domain.password.condiciones_error.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidadorDeContrasenia {

    private List<CondicionesErrorContrasenia> condiciones;

    public ValidadorDeContrasenia() {
        this.condiciones = new ArrayList<CondicionesErrorContrasenia>();
        this.condiciones.add( new ContraseniasVulnerable());
        this.condiciones.add( new PalabrasDelContexto() );
        this.condiciones.add( new ContraseniaCorta() );
        this.condiciones.add( new ContraseniaLarga() );
        this.condiciones.add( new CaractereSecuencial() );
        this.condiciones.add( new CarecteresRepetidos() );
    }

    public List<String> mensajesDeError(InfoUsuario infoUsuario){
        return condiciones.stream().filter(condicion -> condicion.cumpleError(infoUsuario) ).map(condicion -> condicion.mensajeDeError()).collect(Collectors.toList());
    }

    public Boolean contraseniaValida(InfoUsuario infoUsuario){
        return !condiciones.stream().allMatch(condicion -> condicion.cumpleError(infoUsuario));
    }
}
