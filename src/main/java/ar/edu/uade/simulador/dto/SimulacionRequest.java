package ar.edu.uade.simulador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SimulacionRequest(
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = "[A-Za-z0-9._-]{1,20}", message = "debe tener un formato válido")
        String codigoPlanOrigen,
        @NotBlank(message = "no debe estar vacío")
        @Pattern(regexp = "[A-Za-z0-9._-]{1,20}", message = "debe tener un formato válido")
        String codigoPlanDestino) {
}
