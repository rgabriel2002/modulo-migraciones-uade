package ar.edu.uade.simulador.dto;

import jakarta.validation.constraints.NotBlank;

public record SimulacionRequest(
        @NotBlank(message = "no debe estar vacío") String codigoPlanOrigen,
        @NotBlank(message = "no debe estar vacío") String codigoPlanDestino) {
}
