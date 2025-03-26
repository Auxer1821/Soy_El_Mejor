package Converters.TipoDocumento;

import domain.identificador.TipoDocumento;

public class ConverterDocumento {
    public static TipoDocumento convertirDocumento(String documento){
        switch (documento){
            case "libreta-civica" : return TipoDocumento.LIBRETACIVICA;
            case "dni" : return TipoDocumento.DNI;
            case "libreta-electronica" : return TipoDocumento.LIBRETAELECTRONICA;
        }
        return null;
    }
}
