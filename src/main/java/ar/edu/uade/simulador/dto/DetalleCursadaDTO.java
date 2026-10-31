package ar.edu.uade.simulador.dto;

import java.math.BigDecimal;

public record DetalleCursadaDTO(Short anio, Short cuatrimestre, String condicion, BigDecimal nota) {
}
