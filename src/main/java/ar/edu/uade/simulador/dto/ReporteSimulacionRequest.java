package ar.edu.uade.simulador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Cuerpo del reporte PDF: el legajo para el encabezado y la respuesta de la
 * simulación tal cual la vio el usuario (no se re-ejecuta el motor).
 */
public record ReporteSimulacionRequest(
        @NotBlank(message = "no debe estar vacío") String legajo,
        @NotNull(message = "no debe estar vacío") SimulacionResponse simulacion) {
}
