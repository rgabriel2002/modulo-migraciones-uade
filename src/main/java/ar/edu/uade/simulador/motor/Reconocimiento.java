package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Materia;
import java.util.List;

public record Reconocimiento(
        EstadoSimulado estado,
        TipoReconocimiento tipo,
        String respaldo,
        List<Materia> materiasOrigen) {

    public Reconocimiento {
        materiasOrigen = List.copyOf(materiasOrigen);
    }
}
