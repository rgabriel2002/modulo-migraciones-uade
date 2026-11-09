package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.EquivalenciaDTO;
import ar.edu.uade.simulador.servicio.EquivalenciaService;
import ar.edu.uade.simulador.validacion.Validaciones;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/equivalencias")
@RequiredArgsConstructor
@Validated
public class EquivalenciaController {

    private final EquivalenciaService equivalenciaService;

    @GetMapping
    public ResponseEntity<List<EquivalenciaDTO>> listar(
            @RequestParam(required = false)
            @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                    message = "el código de plan es inválido")
            String planOrigen,
            @RequestParam(required = false)
            @Pattern(regexp = Validaciones.PATRON_CODIGO_PLAN_O_VACIO,
                    message = "el código de plan es inválido")
            String planDestino) {
        return ResponseEntity.ok(equivalenciaService.listar(planOrigen, planDestino));
    }
}
