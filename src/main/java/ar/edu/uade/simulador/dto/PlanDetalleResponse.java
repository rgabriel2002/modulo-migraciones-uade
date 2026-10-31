package ar.edu.uade.simulador.dto;

import java.util.List;

public record PlanDetalleResponse(
        String codigoPlan,
        String carrera,
        List<MateriaPlanDTO> materias) {
}
