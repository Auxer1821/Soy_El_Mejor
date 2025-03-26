package services.cargaMasiva;

import Converters.TipoDocumento.CSVConverter;
import domain.colaboraciones.*;
import domain.comunicaciones.Contacto;
import domain.comunicaciones.Mensaje;
import domain.comunicaciones.Notificador;
import domain.comunicaciones.TipoDeContacto;
import domain.sistemaPuntuacion.ColaboracionRealizada;
import domain.sistemaPuntuacion.MotivoColaboracionRealizada;
import domain.ubicaciones.Direccion;
import domain.usuario.colaborador.Humana;
import lombok.Getter;
import lombok.Setter;
import repositorios.RepoGenerico;
import repositorios.colaboradores.RepoHumana;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Getter
@Setter
public class MigracionesService {
    private RepoHumana repoHumana;
    private RepoGenerico repoGenerico;

    public MigracionesService(RepoHumana repoHumana, RepoGenerico repoGenerico) {
        this.repoHumana = repoHumana;
        this.repoGenerico = repoGenerico;
    }

    public void registrarColaboracionCSV(ColaboracionCSV colaboracion) {
        repoHumana.beginTransaction();
        try {
            Optional<Humana> Ohumana = repoHumana.buscarColaborador(
                    colaboracion.getNombre(),
                    colaboracion.getApellido(),
                    colaboracion.getDocumento(),
                    colaboracion.getMail());

            Humana humana;
            if (Ohumana.isEmpty()) {
                humana = new Humana(
                        generarUsernameTemporal(colaboracion.getNombre(), colaboracion.getApellido()),
                        "tu_contrasenia",
                        colaboracion.getMail(),
                        colaboracion.getDocumento(),
                        CSVConverter.convertirDocumento(colaboracion.getTipo()),
                        colaboracion.getNombre(),
                        colaboracion.getApellido(),
                        new Direccion("tu_direccion")
                );

                Contacto contacto = new Contacto(TipoDeContacto.EMAIL, humana.getEmail(), humana);

                repoHumana.persist(humana);
                repoHumana.persist(contacto);

                StringBuilder mensaje = new StringBuilder();

                Notificador.notificar(new Mensaje(mensaje
                        .append("Estimado colaborador").append("\n")
                        .append("Nos comunicamos con usted porque se le ha creado una cuenta en nuestro sistema.")
                        .append("Le pedimos por favor que ingrese a este link en cuanto tenga tiempo para completar los datos restantes.").append("\n")
                        .append("https://2024-tpa-ma-ma-grupo-14-production.up.railway.app/registrarse/humana/").append(humana.getId())
                        .append("\n").append("Desde ya muchas gracias.").append("\n\n Solidary Fridge \n 2024 Solidarity Fridge. Todos los derechos reservados.").toString(),
                        "Información para nuevos usuarios"), contacto);

            } else {
                humana = Ohumana.get();
            }

            ColaboracionRealizada colabo = null;

            switch (colaboracion.getFormaColaboracion()) {
                case "DINERO" -> {
                    if (colaboracion.getCantidad() == null || colaboracion.getCantidad() <= 0) {
                        throw new IllegalArgumentException("La cantidad para donación de dinero es inválida.");
                    }
                    if (colaboracion.getFechaColaboracion() == null) {
                        throw new IllegalArgumentException("La fecha de colaboración para donación de dinero es inválida.");
                    }

                    DonacionDinero donacionDinero = new DonacionDinero(
                            colaboracion.getCantidad(),
                            FrecuenciaDeDonacion.UNICO,
                            colaboracion.getFechaColaboracion().atStartOfDay(),
                            humana
                    );
                    repoHumana.persist(donacionDinero);
                    colabo = new ColaboracionRealizada(MotivoColaboracionRealizada.DONACIONDINERO, donacionDinero);
                }
                case "DONACION_VIANDAS" -> {
                    if (colaboracion.getCantidad() == null || colaboracion.getCantidad() <= 0) {
                        throw new IllegalArgumentException("La cantidad para donación de viandas es inválida.");
                    }
                    if (colaboracion.getFechaColaboracion() == null) {
                        throw new IllegalArgumentException("La fecha de colaboración para donación de viandas es inválida.");
                    }

                    for (int i = 0; i < colaboracion.getCantidad(); i++) {
                        DonacionVianda donacionVianda = new DonacionVianda(
                                humana,
                                colaboracion.getFechaColaboracion().atStartOfDay()
                        );
                        repoHumana.persist(donacionVianda);
                        colabo = new ColaboracionRealizada(MotivoColaboracionRealizada.DONACIONVIANDA, donacionVianda);
                    }
                }
                case "REDISTRIBUCION_VIANDAS" -> {
                    if (colaboracion.getCantidad() == null || colaboracion.getCantidad() <= 0) {
                        throw new IllegalArgumentException("La cantidad para redistribución de viandas es inválida.");
                    }
                    if (colaboracion.getFechaColaboracion() == null) {
                        throw new IllegalArgumentException("La fecha de colaboración para redistribución de viandas es inválida.");
                    }

                    DistribucionViandas distribucionViandas = new DistribucionViandas(
                            colaboracion.getCantidad(),
                            colaboracion.getFechaColaboracion().atStartOfDay(),
                            humana
                    );
                    repoHumana.persist(distribucionViandas);
                    colabo = new ColaboracionRealizada(MotivoColaboracionRealizada.DISTRIBUCIONVIANDA, distribucionViandas);
                }
                case "ENTREGA_TARJETAS" -> {
                    if (colaboracion.getCantidad() == null || colaboracion.getCantidad() <= 0) {
                        throw new IllegalArgumentException("La cantidad para entrega de tarjetas es inválida.");
                    }
                    if (colaboracion.getFechaColaboracion() == null) {
                        throw new IllegalArgumentException("La fecha de colaboración para entrega de tarjetas es inválida.");
                    }

                    for (int i = 0; i < colaboracion.getCantidad(); i++) {
                        RegistroDePersonasVulnerables registroDePersonasVulnerables = new RegistroDePersonasVulnerables(
                                humana,
                                colaboracion.getFechaColaboracion()
                        );
                        repoHumana.persist(registroDePersonasVulnerables);
                        colabo = new ColaboracionRealizada(MotivoColaboracionRealizada.TARJETAENTREGADA, registroDePersonasVulnerables);
                    }
                }
                default ->
                        throw new IllegalArgumentException("Forma de colaboración desconocida: " + colaboracion.getFormaColaboracion());
            }


            if (colabo != null) {
                Double puntos = colabo.puntosGanados();

                repoHumana.actualizarPuntos(humana.getId(), puntos);
            }

            repoHumana.persist(colabo);

            repoHumana.commitTransaction();
        } catch (Exception e) {
            repoHumana.rollbackTransaction();
            throw new RuntimeException(e);
        }

    }

    public static String generarUsernameTemporal(String str1, String str2) {
        str1 = str1.trim().toLowerCase();
        str2 = str2.trim().toLowerCase();

        String base = str1 + str2;

        if (base.length() < 8) {
            base = completarConAleatorios(base, 8 - base.length());
        }

        if (base.length() > 16) {
            base = base.substring(0, 16);
        }

        if (base.length() < 16) {
            base = completarConAleatorios(base, 16 - base.length());
        }
        return base;
    }

    private static String completarConAleatorios(String base, int cantidad) {
        Random random = new Random();
        String caracteres = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder resultado = new StringBuilder(base);

        for (int i = 0; i < cantidad; i++) {
            char randomChar = caracteres.charAt(random.nextInt(caracteres.length()));
            resultado.append(randomChar);
        }

        return resultado.toString();
    }

}
