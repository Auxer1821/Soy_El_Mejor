package domain.sistemaPuntuacion.factory;

import domain.colaboraciones.*;
import domain.sistemaPuntuacion.ColaboracionRealizada;
import domain.sistemaPuntuacion.MotivoColaboracionRealizada;

public class ColaboracionRealizadaFactory {
    public static ColaboracionRealizada create(DonacionVianda colaboracion){
        ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada();
        colaboracionRealizada.setDonacionVianda(colaboracion);
        colaboracionRealizada.setMotivo(MotivoColaboracionRealizada.DONACIONVIANDA);
        return colaboracionRealizada;
    }

    public static ColaboracionRealizada create(DonacionDinero colaboracion){
        ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada();
        colaboracionRealizada.setDonacionDinero(colaboracion);
        colaboracionRealizada.setMotivo(MotivoColaboracionRealizada.DONACIONDINERO);
        return colaboracionRealizada;
    }

    public static ColaboracionRealizada create(EncargoHeladera colaboracion){
        ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada();
        colaboracionRealizada.setEncargoHeladera(colaboracion);
        colaboracionRealizada.setMotivo(MotivoColaboracionRealizada.ENCARGOHELADERA);
        return colaboracionRealizada;
    }

    public static ColaboracionRealizada create(RegistroDePersonasVulnerables colaboracion){
        ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada();
        colaboracionRealizada.setRegistroDePersona(colaboracion);
        colaboracionRealizada.setMotivo(MotivoColaboracionRealizada.TARJETAENTREGADA);
        return colaboracionRealizada;
    }

    public static ColaboracionRealizada create(DistribucionViandas colaboracion){
        ColaboracionRealizada colaboracionRealizada = new ColaboracionRealizada();
        colaboracionRealizada.setViandasDistribuidas(colaboracion);
        colaboracionRealizada.setMotivo(MotivoColaboracionRealizada.DISTRIBUCIONVIANDA);
        return colaboracionRealizada;
    }
}
