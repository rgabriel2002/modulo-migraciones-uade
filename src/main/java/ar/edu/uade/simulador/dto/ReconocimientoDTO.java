package ar.edu.uade.simulador.dto;

import java.util.List;

public record ReconocimientoDTO(String tipo, String respaldo, List<MateriaRefDTO> materiasOrigen) {
}
