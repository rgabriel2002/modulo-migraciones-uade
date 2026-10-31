package ar.edu.uade.simulador.errores;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String mensaje,
        String ruta) {
}
