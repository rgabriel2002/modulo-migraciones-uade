package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dto.ItemSimuladoDTO;
import ar.edu.uade.simulador.dto.MateriaRefDTO;
import ar.edu.uade.simulador.dto.ReporteSimulacionRequest;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Genera el PDF del reporte de equivalencias a partir de la respuesta de la
 * simulación que el usuario ya vio. Columnas del acta:
 * código origen, plan origen, materia origen, condición, código destino,
 * plan destino y materia destino.
 */
@Service
public class ReportePdfService {

    private static final Font TITULO = new Font(Font.HELVETICA, 14, Font.BOLD);
    private static final Font INFO = new Font(Font.HELVETICA, 9);
    private static final Font NEGRITA = new Font(Font.HELVETICA, 8.5f, Font.BOLD);
    private static final Font CELDA = new Font(Font.HELVETICA, 8.5f);
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Anchos relativos de las 7 columnas del acta (suman ~11.9, se normalizan al 100%).
    private static final float ANCHO_CODIGO = 1.15f;
    private static final float ANCHO_PLAN = 0.9f;
    private static final float ANCHO_MATERIA = 3.1f;
    private static final float ANCHO_CONDICION = 1.5f;

    public byte[] generar(ReporteSimulacionRequest request) {
        SimulacionResponse s = request.simulacion();
        ByteArrayOutputStream salida = new ByteArrayOutputStream();

        try {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter.getInstance(doc, salida);
            doc.open();

            Paragraph titulo = new Paragraph(
                    "Simulación de Migración de Plan de Estudios — Reporte de equivalencias",
                    TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(titulo);

            Paragraph info = new Paragraph(
                    "Legajo: " + request.legajo()
                            + "    Origen: " + s.planOrigen().carrera()
                            + " (Plan " + s.planOrigen().codigo() + ")"
                            + "    Destino: " + s.planDestino().carrera()
                            + " (Plan " + s.planDestino().codigo() + ")",
                    INFO);
            info.setAlignment(Element.ALIGN_CENTER);
            info.setSpacingBefore(6);
            info.setSpacingAfter(10);
            doc.add(info);

            doc.add(tabla(s));

            Paragraph pie = new Paragraph(
                    "Aprobadas: " + s.resumen().aprobadas() + " de " + s.items().size()
                            + " · Reconocidas: " + s.resumen().materiasReconocidas()
                            + " · Final pendiente: " + s.resumen().pendientesDeFinal()
                            + " · Pendientes de cursado: " + s.resumen().pendientesDeCursado()
                            + " · Generado el " + LocalDate.now().format(FECHA),
                    INFO);
            pie.setSpacingBefore(10);
            doc.add(pie);

            doc.close();
        } catch (DocumentException e) {
            throw new IllegalStateException("No se pudo generar el PDF del reporte", e);
        }
        return salida.toByteArray();
    }

    private PdfPTable tabla(SimulacionResponse s) {
        PdfPTable tabla = new PdfPTable(7);
        // Código origen · Plan · Materia origen · Condición · Código destino · Plan · Materia destino
        tabla.setWidths(new float[]{
                ANCHO_CODIGO, ANCHO_PLAN, ANCHO_MATERIA, ANCHO_CONDICION,
                ANCHO_CODIGO, ANCHO_PLAN, ANCHO_MATERIA
        });
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(4);

        String[] encabezados = {
            "Código (origen)", "Plan Origen", "Materia (origen)",
            "Condición", "Código (destino)", "Plan", "Materia (destino)"
        };
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, NEGRITA));
            celda.setBackgroundColor(new Color(0xEA, 0xF2, 0xFB));
            celda.setPadding(5);
            tabla.addCell(celda);
        }

        for (ItemSimuladoDTO item : s.items()) {
            String estado = item.estado();
            List<MateriaRefDTO> origenes = item.reconocimiento() == null
                    ? List.of()
                    : item.reconocimiento().materiasOrigen();

            if (origenes.isEmpty()) {
                fila(tabla, "-", "-", "-", estado, item, s.planDestino().codigo());
                continue;
            }
            for (MateriaRefDTO origen : origenes) {
                fila(tabla, origen.codigo(), origen.nombre(), s.planOrigen().codigo(),
                        estado, item, s.planDestino().codigo());
            }
        }
        return tabla;
    }

    /** Una fila por materia origen reconocida; el destino se repite en cada fila. */
    private void fila(
            PdfPTable tabla, String codigoOrigen, String materiaOrigen,
            String planOrigen, String estado, ItemSimuladoDTO item,
            String codigoPlanDestino) {
        tabla.addCell(celda(codigoOrigen));
        tabla.addCell(celda(planOrigen));
        tabla.addCell(celda(materiaOrigen));
        tabla.addCell(celdaCondicion(estado));
        tabla.addCell(celda(item.codigo()));
        tabla.addCell(celda(codigoPlanDestino));
        tabla.addCell(celda(item.nombre()));
    }

    /** Celda de condición con la misma paleta de estados que la UI. */
    private PdfPCell celdaCondicion(String estado) {
        Color fondo = switch (estado) {
            case "APROBADA" -> new Color(0x9f, 0xdc, 0x9f);
            case "FINAL" -> new Color(0xff, 0xe0, 0x66);
            case "CURSANDO" -> new Color(0xea, 0xf2, 0xfb);
            default -> Color.WHITE;
        };
        Color texto = switch (estado) {
            case "APROBADA" -> new Color(0x1f, 0x5a, 0x2f);
            case "FINAL" -> new Color(0x8a, 0x2f, 0x0f);
            case "CURSANDO" -> new Color(0x0f, 0x54, 0x8d);
            default -> Color.BLACK;
        };
        PdfPCell celda = new PdfPCell(new Phrase(
                etiquetaEstado(estado),
                new Font(Font.HELVETICA, 8.5f, Font.NORMAL, texto)));
        celda.setBackgroundColor(fondo);
        celda.setPadding(4);
        return celda;
    }

    private PdfPCell celda(String texto) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, CELDA));
        celda.setPadding(4);
        return celda;
    }

    private String etiquetaEstado(String estado) {
        return switch (estado) {
            case "APROBADA" -> "Aprobada";
            case "FINAL" -> "Final pendiente";
            case "CURSANDO" -> "Cursando";
            case "A_CURSAR" -> "A cursar";
            default -> estado;
        };
    }
}
