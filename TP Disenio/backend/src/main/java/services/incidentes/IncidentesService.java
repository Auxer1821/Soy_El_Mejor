package services.incidentes;

import controllers.DTOs.heladera.IncidenteDTO;
import controllers.DTOs.tecnicos.VisitasDTO;
import domain.heladera.incidente.Incidente;
import domain.usuario.tecnico.Visita;
import repositorios.visitas.RepoVisitas;

import java.util.List;
import java.util.stream.Collectors;

public class IncidentesService {

    private final RepoVisitas repoVisitas;

    public IncidentesService(RepoVisitas repoVisitas) {
        this.repoVisitas = repoVisitas;
    }

    public List<IncidenteDTO> transformarAIncidenteDTO(List<Incidente> incidentes) {
        return incidentes.stream()
                .map(this::convertirAIncidenteDTO)
                .collect(Collectors.toList());
    }

    private IncidenteDTO convertirAIncidenteDTO(Incidente incidente) {
        IncidenteDTO dto = new IncidenteDTO();
        dto.setDescripcion(incidente.getDescripcion());
        dto.setFecha(incidente.getFecha_incidente());

        List<Visita> visitas = repoVisitas.buscarVisitasPorIncidenteId(incidente.getId());
        List<VisitasDTO> visitasDTO = visitas.stream()
                .map(this::convertirAVisitasDTO)
                .collect(Collectors.toList());

        dto.setVisitas(visitasDTO);
        return dto;
    }

    private VisitasDTO convertirAVisitasDTO(Visita visita) {
        VisitasDTO visitasDTO = new VisitasDTO();
        visitasDTO.setDescripcion(visita.getDescripcion());
        visitasDTO.setEstado(visita.getEstado().toString());
        visitasDTO.setFecha(visita.getFecha().toString());
        visitasDTO.setFoto_path(visita.getFoto());
        visitasDTO.setTecnico_cuil(visita.getTecnico().getCuil());
        return visitasDTO;
    }
}
