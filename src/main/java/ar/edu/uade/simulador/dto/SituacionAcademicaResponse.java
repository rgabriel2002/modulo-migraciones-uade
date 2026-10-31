package ar.edu.uade.simulador.dto;

import java.util.List;

public record SituacionAcademicaResponse(
        String legajo,
        String codigoPlan,
        List<MateriaEstadoDTO> materias) {
}
