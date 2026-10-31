package ar.edu.uade.simulador.dto;

public record ResumenDTO(
        int aprobadas,
        int pendientesDeFinal,
        int pendientesDeCursado,
        int materiasReconocidas,
        int pendientesPlanActual,
        int pendientesPlanNuevo,
        TituloIntermedioDTO tituloIntermedio) {
}
