package ar.edu.uade.simulador.errores;

import org.springframework.http.HttpStatus;

public class MigracionNoPermitidaException extends NegocioException {

    public MigracionNoPermitidaException(String mensaje) {
        super(HttpStatus.CONFLICT, mensaje);
    }
}
