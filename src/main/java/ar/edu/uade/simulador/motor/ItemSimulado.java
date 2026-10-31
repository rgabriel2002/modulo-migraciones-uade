package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.PlanMateria;

public record ItemSimulado(PlanMateria materiaDestino, EstadoSimulado estado, Reconocimiento reconocimiento) {

    public static ItemSimulado aCursar(PlanMateria destino) {
        return new ItemSimulado(destino, EstadoSimulado.A_CURSAR, null);
    }

    public static ItemSimulado reconocido(PlanMateria destino, Reconocimiento reconocimiento) {
        return new ItemSimulado(destino, reconocimiento.estado(), reconocimiento);
    }

    public boolean estaReconocida() {
        return reconocimiento != null;
    }
}
