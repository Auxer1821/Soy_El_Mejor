package domain.reportes.type;
import config.ServiceLocator;
import domain.heladera.Heladera;
import domain.viandas.Vianda;
import lombok.Getter;
import lombok.Setter;
import repositorios.heladera.RepoMovimientos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReporteViandasPorColaborador {
    private ArrayList<CantidadViandasPorColaborador> cantidadViandasPorColaboradorLista = new ArrayList<>();

    private void actualizarColaboradorEnLista(Vianda vianda) {
        String colaboradorId = vianda.getColaborador().getId().toString();
        String colaboradorNombre = vianda.getColaborador().getNombre();
        boolean colaboradorEncontrado = false;

        for (CantidadViandasPorColaborador item : cantidadViandasPorColaboradorLista) {
            if (item.getId().equals(colaboradorId)) {
                item.incrementarCantidad();
                colaboradorEncontrado = true;
                break;
            }
        }

        if (!colaboradorEncontrado) {
            cantidadViandasPorColaboradorLista.add(new CantidadViandasPorColaborador(colaboradorId, colaboradorNombre));
        }
    }

    public void generarReporte(Heladera heladera){
        LocalDateTime haceUnaSemana = LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        List<Vianda> viandasUltimaSemana = ServiceLocator.instanceOf(RepoMovimientos.class)
                .viandas(heladera)
                .stream()
                .filter(vianda -> vianda.getFechaDeColaboracion().isAfter(haceUnaSemana))
                .toList();

        viandasUltimaSemana.forEach(this::actualizarColaboradorEnLista);
    }
}
