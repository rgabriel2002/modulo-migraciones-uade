package ar.edu.uade.simulador.dto;

import ar.edu.uade.simulador.validacion.Validaciones;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SimulacionRequest(
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                message = "debe tener un formato válido")
        String codigoPlanOrigen,
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                message = "debe tener un formato válido")
        String codigoPlanDestino) {
}
