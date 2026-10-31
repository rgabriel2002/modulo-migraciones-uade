package ar.edu.uade.simulador.dto;

import java.time.LocalDate;
import java.util.List;

public record EquivalenciaDTO(
        String resolucion,
        String planOrigen,
        String planDestino,
        List<MateriaRefDTO> materiasOrigen,
        MateriaRefDTO materiaDestino,
        VigenciaDTO vigencia,
        boolean vigente) {

    public record VigenciaDTO(LocalDate desde, LocalDate hasta) {
    }
}
