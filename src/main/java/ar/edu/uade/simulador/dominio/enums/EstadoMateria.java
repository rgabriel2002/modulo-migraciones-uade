package ar.edu.uade.simulador.dominio.enums;

public enum EstadoMateria {
    APROBADA(3),
    FINAL(2),
    CURSANDO(1);

    private final int rango;

    EstadoMateria(int rango) {
        this.rango = rango;
    }

    public int rango() {
        return rango;
    }
}
