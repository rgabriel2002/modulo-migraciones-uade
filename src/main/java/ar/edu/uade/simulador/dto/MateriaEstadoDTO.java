package ar.edu.uade.simulador.dto;

import java.math.BigDecimal;

public record MateriaEstadoDTO(
        String codigo,
        String nombre,
        String estado,
        BigDecimal notaFinal) {
}
