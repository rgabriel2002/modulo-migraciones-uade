package ar.edu.uade.simulador.dto;

import jakarta.validation.constraints.NotBlank;

public record SimulacionRequest(@NotBlank String codigoPlanDestino) {
}
