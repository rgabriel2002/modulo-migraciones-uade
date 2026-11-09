package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.SimulacionRequest;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import ar.edu.uade.simulador.servicio.SimulacionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simulaciones")
@RequiredArgsConstructor
@Validated
public class SimulacionController {

    private final SimulacionService simulacionService;

    @PostMapping
    public ResponseEntity<SimulacionResponse> simular(
            @Valid @RequestBody SimulacionRequest request,
            @RequestHeader("X-Legajo")
            @Pattern(regexp = "\\d{7,15}", message = "el legajo debe tener entre 7 y 15 dígitos")
            String legajo) {
        return ResponseEntity.ok(
                simulacionService.simular(legajo, request.codigoPlanOrigen(), request.codigoPlanDestino()));
    }
}
