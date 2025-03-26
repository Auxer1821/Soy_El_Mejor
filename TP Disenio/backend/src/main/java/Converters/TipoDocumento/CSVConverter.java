package Converters.TipoDocumento;

import domain.identificador.TipoDocumento;

public class CSVConverter {
    public static TipoDocumento convertirDocumento(String documento){
        switch (documento){
            case "LC" : return TipoDocumento.LIBRETACIVICA;
            case "DNI" : return TipoDocumento.DNI;
            case "LE" : return TipoDocumento.LIBRETAELECTRONICA;
        }
        return null;
    }

}
