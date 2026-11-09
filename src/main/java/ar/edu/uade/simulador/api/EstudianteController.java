package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.HistorialMateriaDTO;
import ar.edu.uade.simulador.dto.SituacionAcademicaResponse;
import ar.edu.uade.simulador.servicio.EstudianteService;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/estudiantes")
@RequiredArgsConstructor
@Validated
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/situacion")
    public ResponseEntity<SituacionAcademicaResponse> obtenerSituacion(
            @RequestHeader("X-Legajo")
            @Pattern(regexp = "\\d{7,15}", message = "el legajo debe tener entre 7 y 15 dígitos")
            String legajo) {
        return ResponseEntity.ok(estudianteService.obtenerSituacion(legajo));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<HistorialMateriaDTO>> obtenerHistorial(
            @RequestHeader("X-Legajo")
            @Pattern(regexp = "\\d{7,15}", message = "el legajo debe tener entre 7 y 15 dígitos")
            String legajo) {
        return ResponseEntity.ok(estudianteService.obtenerHistorial(legajo));
    }
}
