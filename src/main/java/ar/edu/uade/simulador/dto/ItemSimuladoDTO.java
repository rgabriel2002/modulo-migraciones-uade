package ar.edu.uade.simulador.dto;

public record ItemSimuladoDTO(
        String codigo,
        String nombre,
        Short anio,
        Short cuatrimestre,
        Integer cargaHoraria,
        boolean optativa,
        boolean tituloIntermedio,
        String estado,
        ReconocimientoDTO reconocimiento) {
}
