package ar.edu.uade.simulador.api;

import ar.edu.uade.simulador.dto.ReporteSimulacionRequest;
import ar.edu.uade.simulador.servicio.ReportePdfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReportePdfService reportePdfService;

    /**
     * Genera el PDF del reporte con la respuesta de la simulación que el
     * cliente ya obtuvo: el documento refleja exactamente lo que vio el
     * usuario, sin re-ejecutar el motor.
     */
    @PostMapping("/simulacion")
    public ResponseEntity<byte[]> generarReporte(
            @Valid @RequestBody ReporteSimulacionRequest request) {
        byte[] pdf = reportePdfService.generar(request);

        String nombre = String.format(
                "simulacion-%s-a-%s.pdf",
                request.simulacion().planOrigen().codigo(),
                request.simulacion().planDestino().codigo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + nombre + "\"")
                .body(pdf);
    }
}
