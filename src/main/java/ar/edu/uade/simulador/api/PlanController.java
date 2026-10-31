package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.PlanDetalleResponse;
import ar.edu.uade.simulador.dto.PlanResponse;
import ar.edu.uade.simulador.servicio.PlanService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanResponse>> listarPlanesDestino(
            @RequestHeader("X-Legajo") String legajo) {
        return ResponseEntity.ok(planService.listarPlanesDestino(legajo));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PlanDetalleResponse> obtenerPlan(@PathVariable String codigo) {
        return ResponseEntity.ok(planService.obtenerPlan(codigo));
    }
}
