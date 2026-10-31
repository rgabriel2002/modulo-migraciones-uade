package ar.edu.uade.simulador.dto;

import java.math.BigDecimal;

public record MateriaEstadoDTO(
        String codigo,
        String nombre,
        Short anio,
        Short cuatrimestre,
        String estado,
        BigDecimal nota,
        boolean cursada) {
}
