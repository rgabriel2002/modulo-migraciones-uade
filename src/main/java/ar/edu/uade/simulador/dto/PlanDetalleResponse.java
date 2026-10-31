package ar.edu.uade.simulador.dto;

import java.util.List;

public record PlanDetalleResponse(
        String codigoPlan,
        List<MateriaPlanDTO> materias) {
}
