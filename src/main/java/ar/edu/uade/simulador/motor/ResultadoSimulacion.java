package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Materia;
import java.util.List;

public record ResultadoSimulacion(
        List<ItemSimulado> items,
        ResumenSimulacion resumen,
        List<Materia> optativasSinCorrespondencia) {
}
