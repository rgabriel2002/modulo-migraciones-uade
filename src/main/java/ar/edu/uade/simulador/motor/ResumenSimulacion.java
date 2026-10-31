package ar.edu.uade.simulador.motor;

import java.util.List;

public record ResumenSimulacion(
        int aprobadas,
        int pendientesDeFinal,
        int pendientesDeCursado,
        int materiasReconocidas,
        double avanceTituloIntermedio) {

    public static ResumenSimulacion calcular(List<ItemSimulado> items) {
        int aprobadas = contar(items, EstadoSimulado.APROBADA);
        int pendientesDeFinal = contar(items, EstadoSimulado.FINAL);
        int pendientesDeCursado = contar(items, EstadoSimulado.CURSANDO) + contar(items, EstadoSimulado.A_CURSAR);
        int reconocidas = (int) items.stream().filter(ItemSimulado::estaReconocida).count();

        long totalTituloIntermedio = items.stream()
                .filter(item -> item.materiaDestino().isEsTituloIntermedio())
                .count();
        long aprobadasTituloIntermedio = items.stream()
                .filter(item -> item.materiaDestino().isEsTituloIntermedio())
                .filter(item -> item.estado() == EstadoSimulado.APROBADA)
                .count();
        double avance = totalTituloIntermedio == 0
                ? 0.0
                : Math.round(10000.0 * aprobadasTituloIntermedio / totalTituloIntermedio) / 100.0;

        return new ResumenSimulacion(
                aprobadas, pendientesDeFinal, pendientesDeCursado, reconocidas, avance);
    }

    private static int contar(List<ItemSimulado> items, EstadoSimulado estado) {
        return (int) items.stream().filter(item -> item.estado() == estado).count();
    }
}
