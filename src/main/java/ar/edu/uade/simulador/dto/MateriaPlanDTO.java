package ar.edu.uade.simulador.dto;

import java.util.List;

public record MateriaPlanDTO(
        String codigo,
        String nombre,
        Short anio,
        Short cuatrimestre,
        Integer cargaHoraria,
        boolean optativa,
        boolean tituloIntermedio,
        List<CorrelatividadDTO> correlativas) {
}
