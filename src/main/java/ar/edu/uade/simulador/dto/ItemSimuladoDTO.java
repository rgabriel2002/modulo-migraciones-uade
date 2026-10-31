package ar.edu.uade.simulador.dto;

import java.util.List;

public record ItemSimuladoDTO(
        String codigo,
        String nombre,
        Short anioPlan,
        Short cuatrimestre,
        Integer cargaHoraria,
        boolean esOptativa,
        boolean esTituloIntermedio,
        String estado,
        String respaldo,
        List<String> materiasOrigen) {
}
