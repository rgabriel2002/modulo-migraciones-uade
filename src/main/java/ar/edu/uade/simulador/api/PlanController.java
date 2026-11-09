package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.PlanDetalleResponse;
import ar.edu.uade.simulador.dto.PlanResponse;
import ar.edu.uade.simulador.servicio.PlanService;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planes")
@RequiredArgsConstructor
@Validated
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanResponse>> listarPlanesDestino(
            @RequestHeader("X-Legajo")
            @Pattern(regexp = "\\d{7,15}", message = "el legajo debe tener entre 7 y 15 dígitos")
            String legajo,
            @RequestParam(value = "excluir", required = false)
            @Pattern(regexp = "[A-Za-z0-9._-]{1,20}", message = "el código de plan es inválido")
            String excluir) {
        return ResponseEntity.ok(planService.listarPlanesDestino(legajo, excluir));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<PlanDetalleResponse> obtenerPlan(@PathVariable String codigo) {
        return ResponseEntity.ok(planService.obtenerPlan(codigo));
    }
}
