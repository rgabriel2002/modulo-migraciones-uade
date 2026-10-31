package ar.edu.uade.simulador.dto;

import java.util.List;

public record SimulacionResponse(
        PlanRefDTO planOrigen,
        PlanRefDTO planDestino,
        List<ItemSimuladoDTO> items,
        ResumenDTO resumen,
        List<MateriaRefDTO> optativasSinCorrespondencia) {
}
