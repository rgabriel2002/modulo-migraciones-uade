package ar.edu.uade.simulador.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HistorialMateriaDTO(
        MateriaRefDTO materia,
        String estado,
        BigDecimal notaFinal,
        LocalDate fechaAprobacion,
        List<DetalleCursadaDTO> cursadas) {
}
