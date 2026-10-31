package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.EquivalenciaDTO;
import ar.edu.uade.simulador.servicio.EquivalenciaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/equivalencias")
@RequiredArgsConstructor
public class EquivalenciaController {

    private final EquivalenciaService equivalenciaService;

    @GetMapping
    public ResponseEntity<List<EquivalenciaDTO>> listar(
            @RequestParam(required = false) String planOrigen,
            @RequestParam(required = false) String planDestino) {
        return ResponseEntity.ok(equivalenciaService.listar(planOrigen, planDestino));
    }
}
