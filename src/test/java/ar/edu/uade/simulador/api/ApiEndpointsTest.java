package ar.edu.uade.simulador.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.uade.simulador.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ApiEndpointsTest {

    private static final String LEGAJO = "1100001";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void c1DevuelveElPlanConCorrelativas() throws Exception {
        mockMvc.perform(get("/api/v1/planes/1621"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigoPlan").value("1621"))
                .andExpect(jsonPath("$.carrera").value("Ingeniería en Informática"))
                .andExpect(jsonPath("$.materias.length()").value(7))
                .andExpect(jsonPath("$.materias[0].codigo").value("3.4.069"))
                .andExpect(jsonPath("$.materias[0].anio").value(1))
                .andExpect(jsonPath("$.materias[0].cuatrimestre").value(1))
                .andExpect(jsonPath("$.materias[0].cargaHoraria").value(68))
                .andExpect(jsonPath("$.materias[0].optativa").value(false))
                .andExpect(jsonPath("$.materias[0].tituloIntermedio").value(true))
                .andExpect(jsonPath("$.materias[5].codigo").value("9.9.003"))
                .andExpect(jsonPath("$.materias[5].correlativas.length()").value(2))
                .andExpect(jsonPath("$.materias[5].correlativas[0].codigo").value("3.4.069"))
                .andExpect(jsonPath("$.materias[5].correlativas[0].tipo").value("CURSADA"))
                .andExpect(jsonPath("$.materias[5].correlativas[1].codigo").value("9.9.006"))
                .andExpect(jsonPath("$.materias[5].correlativas[1].tipo").value("FINAL"));
    }

    @Test
    void c2DevuelveLaSituacionAcademicaDelAlumno() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/situacion").header("X-Legajo", LEGAJO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legajo").value(LEGAJO))
                .andExpect(jsonPath("$.plan.codigo").value("1121"))
                .andExpect(jsonPath("$.plan.carrera")
                        .value("Tecnicatura Universitaria en Desarrollo de Software"))
                .andExpect(jsonPath("$.materias.length()").value(7))
                .andExpect(jsonPath("$.materias[0].codigo").value("3.4.164"))
                .andExpect(jsonPath("$.materias[0].anio").value(1))
                .andExpect(jsonPath("$.materias[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$.materias[0].nota").value(7.0))
                .andExpect(jsonPath("$.materias[0].cursada").value(true))
                .andExpect(jsonPath("$.materias[4].codigo").value("9.9.002"))
                .andExpect(jsonPath("$.materias[4].estado").value("FINAL"))
                .andExpect(jsonPath("$.materias[4].nota").isEmpty())
                .andExpect(jsonPath("$.materias[4].cursada").value(true));
    }

    @Test
    void c3C4C5C6DevuelvenLaSimulacionCompleta() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanDestino\":\"1621\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planOrigen.codigo").value("1121"))
                .andExpect(jsonPath("$.planDestino.codigo").value("1621"))
                .andExpect(jsonPath("$.items.length()").value(7))
                .andExpect(jsonPath("$.items[0].codigo").value("3.4.069"))
                .andExpect(jsonPath("$.items[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[0].reconocimiento.tipo").value("EQUIVALENCIA"))
                .andExpect(jsonPath("$.items[0].reconocimiento.respaldo").value("Res. 123/21"))
                .andExpect(jsonPath("$.items[0].reconocimiento.materiasOrigen[0].codigo")
                        .value("3.4.225"))
                .andExpect(jsonPath("$.items[1].codigo").value("3.4.164"))
                .andExpect(jsonPath("$.items[1].reconocimiento.tipo").value("MISMA_MATERIA"))
                .andExpect(jsonPath("$.items[2].codigo").value("9.9.001"))
                .andExpect(jsonPath("$.items[2].estado").value("A_CURSAR"))
                .andExpect(jsonPath("$.items[2].reconocimiento").isEmpty())
                .andExpect(jsonPath("$.items[3].codigo").value("9.9.006"))
                .andExpect(jsonPath("$.items[3].estado").value("CURSANDO"))
                .andExpect(jsonPath("$.items[4].codigo").value("9.9.002"))
                .andExpect(jsonPath("$.items[4].estado").value("FINAL"))
                .andExpect(jsonPath("$.items[5].codigo").value("9.9.003"))
                .andExpect(jsonPath("$.items[5].estado").value("A_CURSAR"))
                .andExpect(jsonPath("$.items[6].codigo").value("9.9.004"))
                .andExpect(jsonPath("$.items[6].estado").value("APROBADA"))
                .andExpect(jsonPath("$.resumen.aprobadas").value(3))
                .andExpect(jsonPath("$.resumen.pendientesDeFinal").value(1))
                .andExpect(jsonPath("$.resumen.pendientesDeCursado").value(3))
                .andExpect(jsonPath("$.resumen.materiasReconocidas").value(5))
                .andExpect(jsonPath("$.resumen.pendientesPlanActual").value(2))
                .andExpect(jsonPath("$.resumen.pendientesPlanNuevo").value(4))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.aprobadas").value(2))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.total").value(6))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.avance").value(33))
                .andExpect(jsonPath("$.optativasSinCorrespondencia.length()").value(1))
                .andExpect(jsonPath("$.optativasSinCorrespondencia[0].codigo").value("9.9.005"));
    }

    @Test
    void c7DevuelveLasEquivalenciasCargadas() throws Exception {
        mockMvc.perform(get("/api/v1/equivalencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].resolucion").value("Res. 123/21"))
                .andExpect(jsonPath("$[0].planOrigen").value("1121"))
                .andExpect(jsonPath("$[0].planDestino").value("1621"))
                .andExpect(jsonPath("$[0].materiasOrigen.length()").value(1))
                .andExpect(jsonPath("$[0].materiasOrigen[0].codigo").value("3.4.225"))
                .andExpect(jsonPath("$[0].materiasOrigen[0].nombre")
                        .value("Introducción a la Algoritmia"))
                .andExpect(jsonPath("$[0].materiaDestino.codigo").value("3.4.069"))
                .andExpect(jsonPath("$[0].vigencia.desde").value("2021-03-01"))
                .andExpect(jsonPath("$[0].vigencia.hasta").isEmpty())
                .andExpect(jsonPath("$[0].vigente").value(true));
    }

    @Test
    void c7PermiteFiltrarPorPlan() throws Exception {
        mockMvc.perform(get("/api/v1/equivalencias?planOrigen=1121&planDestino=1621"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/equivalencias?planOrigen=9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void c8DevuelveElHistorialConLasRecursadas() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/historial").header("X-Legajo", LEGAJO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(7))
                .andExpect(jsonPath("$[1].materia.codigo").value("3.4.225"))
                .andExpect(jsonPath("$[1].cursadas.length()").value(1))
                .andExpect(jsonPath("$[1].cursadas[0].condicion").value("PROMOCIONADA"))
                .andExpect(jsonPath("$[1].cursadas[0].nota").value(8.0))
                .andExpect(jsonPath("$[3].materia.codigo").value("9.9.002"))
                .andExpect(jsonPath("$[3].estado").value("FINAL"))
                .andExpect(jsonPath("$[3].cursadas.length()").value(2))
                .andExpect(jsonPath("$[3].cursadas[0].anio").value(2024))
                .andExpect(jsonPath("$[3].cursadas[0].cuatrimestre").value(1))
                .andExpect(jsonPath("$[3].cursadas[0].condicion").value("LIBRE"))
                .andExpect(jsonPath("$[3].cursadas[0].nota").value(3.0))
                .andExpect(jsonPath("$[3].cursadas[1].cuatrimestre").value(2))
                .andExpect(jsonPath("$[3].cursadas[1].condicion").value("REGULAR"))
                .andExpect(jsonPath("$[3].cursadas[1].nota").value(6.0));
    }

    @Test
    void devuelve400SiFaltaElLegajo() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/situacion"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.ruta").value("/api/v1/estudiantes/situacion"));
    }

    @Test
    void devuelve404SiElLegajoNoExiste() throws Exception {
        mockMvc.perform(get("/api/v1/planes").header("X-Legajo", "9999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void devuelve404SiElPlanNoExiste() throws Exception {
        mockMvc.perform(get("/api/v1/planes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el plan 9999"));
    }

    @Test
    void devuelve409SiElPlanDestinoEsElActual() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanDestino\":\"1121\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void devuelve400SiElBodyEsInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanDestino\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("codigoPlanDestino: no debe estar vacío"));
    }

    @Test
    void devuelve400SiElJSONEstaMalFormado() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{bad json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void devuelve415SiElContentTypeNoEsSoportado() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("hola"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415));
    }
}
