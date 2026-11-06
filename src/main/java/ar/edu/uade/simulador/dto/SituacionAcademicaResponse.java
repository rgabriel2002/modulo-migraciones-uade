package ar.edu.uade.simulador.dto;

import java.util.List;

public record SituacionAcademicaResponse(
        String legajo,
        List<PlanRefDTO> planesActivos) {
}
