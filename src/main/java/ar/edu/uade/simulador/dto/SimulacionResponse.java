package ar.edu.uade.simulador.dto;

import java.util.List;

public record SimulacionResponse(
        List<ItemSimuladoDTO> items,
        ResumenDTO resumen,
        List<String> optativasSinCorrespondencia) {
}
