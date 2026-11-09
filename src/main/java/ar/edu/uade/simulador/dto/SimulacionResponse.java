package ar.edu.uade.simulador.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SimulacionResponse(
        @NotNull(message = "no debe estar vacío") PlanRefDTO planOrigen,
        @NotNull(message = "no debe estar vacío") PlanRefDTO planDestino,
        @NotNull(message = "no debe estar vacío") List<ItemSimuladoDTO> items,
        @NotNull(message = "no debe estar vacío") ResumenDTO resumen,
        @NotNull(message = "no debe estar vacío") List<MateriaRefDTO> optativasSinCorrespondencia) {
}
