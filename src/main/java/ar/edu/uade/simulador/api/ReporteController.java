package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.ReporteSimulacionRequest;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import ar.edu.uade.simulador.servicio.ReportePdfService;
import ar.edu.uade.simulador.servicio.SimulacionService;
import ar.edu.uade.simulador.validacion.Validaciones;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Validated
public class ReporteController {

    private final ReportePdfService reportePdfService;
    private final SimulacionService simulacionService;

    /**
     * Genera el PDF del reporte recalculando la simulación en el servidor:
     * el documento sale siempre de datos reales, no de lo que envíe el cliente.
     */
    @PostMapping("/simulacion")
    public ResponseEntity<byte[]> generarReporte(
            @Valid @RequestBody ReporteSimulacionRequest request,
            @RequestHeader("X-Legajo")
            @Pattern(regexp = Validaciones.PATRON_LEGAJO,
                    message = "el legajo debe tener entre 7 y 15 dígitos")
            String legajo) {
        SimulacionResponse simulacion = simulacionService.simular(
                legajo, request.codigoPlanOrigen(), request.codigoPlanDestino());
        byte[] pdf = reportePdfService.generar(legajo, simulacion);

        String nombre = String.format(
                "simulacion-%s-a-%s.pdf",
                simulacion.planOrigen().codigo(),
                simulacion.planDestino().codigo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombre + "\"")
                .body(pdf);
    }
}
