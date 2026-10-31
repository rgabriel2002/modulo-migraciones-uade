package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.enums.EstadoMateria;

public enum EstadoSimulado {
    APROBADA,
    FINAL,
    CURSANDO,
    A_CURSAR;

    public static EstadoSimulado desde(EstadoMateria estado) {
        return switch (estado) {
            case APROBADA -> APROBADA;
            case FINAL -> FINAL;
            case CURSANDO -> CURSANDO;
        };
    }

    public boolean esMejorQue(EstadoSimulado otro) {
        return ordinal() < otro.ordinal();
    }
}
