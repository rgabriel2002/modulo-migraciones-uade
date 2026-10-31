package ar.edu.uade.simulador.dto;

public record MateriaPlanDTO(
        String codigo,
        String nombre,
        Short anioPlan,
        Short cuatrimestre,
        Integer cargaHoraria,
        boolean esOptativa,
        boolean esTituloIntermedio) {
}
