package Converters;

import controllers.DTOs.heladera.HeladeraDTO;
import controllers.DTOs.otros.ReporteDTO;
import domain.heladera.Heladera;
import domain.reportes.ReporteGeneral;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HeladeraConverter {
    public static List<HeladeraDTO> convert(List<Heladera> heladeras) {
        return heladeras.stream()
                .map(heladera -> new HeladeraDTO(
                        heladera.getDireccion().getCoordenadas().getLatitud(),
                        heladera.getDireccion().getCoordenadas().getLongitud(),
                        heladera.getNombre(),
                        heladera.getId(),
                        heladera.getModelo().getMaxCantidadViandas(),
                        heladera.getCantidadViandas(),
                        heladera.getEstado().toString().toLowerCase()
                ))
                .collect(Collectors.toList());
    }

    public static List<ReporteDTO> convertirReportes(List<ReporteGeneral> reportes) {
        return reportes.stream()
                .map(reporte -> new ReporteDTO(
                        reporte.getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                        reporte.getPath()
                ))
                .collect(Collectors.toList());
    }
}

