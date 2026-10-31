package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.HistorialMateriaDTO;
import ar.edu.uade.simulador.dto.SituacionAcademicaResponse;
import ar.edu.uade.simulador.servicio.EstudianteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/situacion")
    public ResponseEntity<SituacionAcademicaResponse> obtenerSituacion(
            @RequestHeader("X-Legajo") String legajo) {
        return ResponseEntity.ok(estudianteService.obtenerSituacion(legajo));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialMateriaDTO>> obtenerHistorial(
            @RequestHeader("X-Legajo") String legajo) {
        return ResponseEntity.ok(estudianteService.obtenerHistorial(legajo));
    }
}
