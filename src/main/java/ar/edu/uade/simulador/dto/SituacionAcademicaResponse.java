package ar.edu.uade.simulador.dto;

import java.util.List;

public record SituacionAcademicaResponse(
        String legajo,
        PlanRefDTO plan,
        List<MateriaEstadoDTO> materias) {
}
