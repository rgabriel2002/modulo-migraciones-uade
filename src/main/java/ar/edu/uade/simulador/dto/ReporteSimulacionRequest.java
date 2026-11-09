package ar.edu.uade.simulador.dto;

import ar.edu.uade.simulador.validacion.Validaciones;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Cuerpo del reporte PDF. Recibe los mismos parámetros que la simulación:
 * el backend recalcula el resultado y genera el PDF con datos propios, para
 * que el documento no dependa de datos enviados por el cliente.
 */
public record ReporteSimulacionRequest(
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                message = "debe tener un formato válido")
        String codigoPlanOrigen,
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                message = "debe tener un formato válido")
        String codigoPlanDestino) {
}
