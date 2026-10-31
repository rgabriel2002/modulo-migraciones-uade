package ar.edu.uade.simulador.motor;

import java.util.List;

public record ResumenSimulacion(
        int aprobadas,
        int pendientesDeFinal,
        int pendientesDeCursado,
        int materiasReconocidas,
        int pendientesPlanActual,
        int pendientesPlanNuevo,
        int aprobadasTituloIntermedio,
        int totalTituloIntermedio,
        int avanceTituloIntermedio) {

    public static ResumenSimulacion calcular(List<ItemSimulado> items, int pendientesPlanActual) {
        int aprobadas = contar(items, EstadoSimulado.APROBADA);
        int pendientesDeFinal = contar(items, EstadoSimulado.FINAL);
        int pendientesDeCursado = contar(items, EstadoSimulado.CURSANDO)
                + contar(items, EstadoSimulado.A_CURSAR);
        int reconocidas = (int) items.stream().filter(ItemSimulado::estaReconocida).count();

        int totalTituloIntermedio = (int) items.stream()
                .filter(item -> item.materiaDestino().isEsTituloIntermedio())
                .count();
        int aprobadasTituloIntermedio = (int) items.stream()
                .filter(item -> item.materiaDestino().isEsTituloIntermedio())
                .filter(item -> item.estado() == EstadoSimulado.APROBADA)
                .count();
        int avance = totalTituloIntermedio == 0
                ? 0
                : (int) Math.round(100.0 * aprobadasTituloIntermedio / totalTituloIntermedio);

        return new ResumenSimulacion(
                aprobadas,
                pendientesDeFinal,
                pendientesDeCursado,
                reconocidas,
                pendientesPlanActual,
                items.size() - aprobadas,
                aprobadasTituloIntermedio,
                totalTituloIntermedio,
                avance);
    }

    private static int contar(List<ItemSimulado> items, EstadoSimulado estado) {
        return (int) items.stream().filter(item -> item.estado() == estado).count();
    }
}
