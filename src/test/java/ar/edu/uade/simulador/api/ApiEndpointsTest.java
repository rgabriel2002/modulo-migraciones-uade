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
                .andExpect(jsonPath("$.materias.length()").value(51))
                .andExpect(jsonPath("$.materias[0].codigo").value("2.1.002"))
                .andExpect(jsonPath("$.materias[0].anio").value(1))
                .andExpect(jsonPath("$.materias[0].cuatrimestre").value(1))
                .andExpect(jsonPath("$.materias[0].cargaHoraria").value(68))
                .andExpect(jsonPath("$.materias[0].optativa").value(false))
                .andExpect(jsonPath("$.materias[0].tituloIntermedio").value(true))
                .andExpect(jsonPath("$.materias[9].codigo").value("3.4.071"))
                .andExpect(jsonPath("$.materias[9].correlativas.length()").value(1))
                .andExpect(jsonPath("$.materias[9].correlativas[0].codigo").value("3.4.069"))
                .andExpect(jsonPath("$.materias[9].correlativas[0].tipo").value("CURSADA"))
                .andExpect(jsonPath("$.materias[40].codigo").value("OPT-ING-I"))
                .andExpect(jsonPath("$.materias[40].optativa").value(true));
    }

    @Test
    void c2DevuelveLaSituacionAcademicaDelAlumno() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/situacion").header("X-Legajo", LEGAJO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legajo").value(LEGAJO))
                .andExpect(jsonPath("$.planesActivos.length()").value(1))
                .andExpect(jsonPath("$.planesActivos[0].codigo").value("1121"))
                .andExpect(jsonPath("$.planesActivos[0].carrera")
                        .value("Tecnicatura Universitaria en Desarrollo de Software"))
                .andExpect(jsonPath("$.planesActivos[0].facultad")
                        .value("Facultad de Ingeniería y Ciencias Exactas"));
    }

    @Test
    void c2DevuelveTodosLosPlanesActivosDeUnAlumnoMultiCarrera() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/situacion").header("X-Legajo", "1100002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legajo").value("1100002"))
                .andExpect(jsonPath("$.planesActivos.length()").value(2))
                .andExpect(jsonPath("$.planesActivos[0].codigo").value("1621"))
                .andExpect(jsonPath("$.planesActivos[0].facultad")
                        .value("Facultad de Ingeniería y Ciencias Exactas"))
                .andExpect(jsonPath("$.planesActivos[1].codigo").value("5010"))
                .andExpect(jsonPath("$.planesActivos[1].carrera").value("Contador Público"))
                .andExpect(jsonPath("$.planesActivos[1].facultad")
                        .value("Facultad de Ciencias Económicas"));
    }

    @Test
    void c3C4C5C6DevuelvenLaSimulacionCompleta() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1121\",\"codigoPlanDestino\":\"1621\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planOrigen.codigo").value("1121"))
                .andExpect(jsonPath("$.planOrigen.facultad")
                        .value("Facultad de Ingeniería y Ciencias Exactas"))
                .andExpect(jsonPath("$.planDestino.codigo").value("1621"))
                .andExpect(jsonPath("$.items.length()").value(51))
                .andExpect(jsonPath("$.items[3].codigo").value("3.4.069"))
                .andExpect(jsonPath("$.items[3].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[3].reconocimiento.tipo").value("EQUIVALENCIA"))
                .andExpect(jsonPath("$.items[3].reconocimiento.respaldo").value("Res. 123/21"))
                .andExpect(jsonPath("$.items[3].reconocimiento.materiasOrigen[0].codigo")
                        .value("3.4.225"))
                .andExpect(jsonPath("$.items[4].codigo").value("3.4.164"))
                .andExpect(jsonPath("$.items[4].reconocimiento.tipo").value("MISMA_MATERIA"))
                .andExpect(jsonPath("$.items[10].codigo").value("3.4.072"))
                .andExpect(jsonPath("$.items[10].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[14].codigo").value("3.4.075"))
                .andExpect(jsonPath("$.items[14].estado").value("FINAL"))
                .andExpect(jsonPath("$.items[15].codigo").value("3.4.207"))
                .andExpect(jsonPath("$.items[15].estado").value("CURSANDO"))
                .andExpect(jsonPath("$.resumen.aprobadas").value(3))
                .andExpect(jsonPath("$.resumen.pendientesDeFinal").value(1))
                .andExpect(jsonPath("$.resumen.pendientesDeCursado").value(47))
                .andExpect(jsonPath("$.resumen.materiasReconocidas").value(5))
                .andExpect(jsonPath("$.resumen.pendientesPlanActual").value(14))
                .andExpect(jsonPath("$.resumen.pendientesPlanNuevo").value(48))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.aprobadas").value(3))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.total").value(31))
                .andExpect(jsonPath("$.resumen.tituloIntermedio.avance").value(10))
                .andExpect(jsonPath("$.optativasSinCorrespondencia.length()").value(1))
                .andExpect(jsonPath("$.optativasSinCorrespondencia[0].codigo").value("OPT-TDS-I"));
    }

    @Test
    void c7DevuelveLasEquivalenciasCargadas() throws Exception {
        mockMvc.perform(get("/api/v1/equivalencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].resolucion").value("Res. 456/22"))
                .andExpect(jsonPath("$[0].planOrigen").value("1621"))
                .andExpect(jsonPath("$[0].planDestino").value("5010"))
                .andExpect(jsonPath("$[0].materiasOrigen.length()").value(1))
                .andExpect(jsonPath("$[0].materiasOrigen[0].codigo").value("3.4.164"))
                .andExpect(jsonPath("$[0].materiasOrigen[0].nombre")
                        .value("Sistemas de Información I"))
                .andExpect(jsonPath("$[0].materiaDestino.codigo").value("1.1.144"))
                .andExpect(jsonPath("$[0].materiaDestino.nombre")
                        .value("Diseño y Auditoría de Sistemas de Información"))
                .andExpect(jsonPath("$[1].resolucion").value("Res. 123/21"))
                .andExpect(jsonPath("$[1].planOrigen").value("1121"))
                .andExpect(jsonPath("$[1].planDestino").value("1621"))
                .andExpect(jsonPath("$[1].materiasOrigen[0].codigo").value("3.4.225"))
                .andExpect(jsonPath("$[1].materiaDestino.codigo").value("3.4.069"))
                .andExpect(jsonPath("$[1].vigencia.desde").value("2021-03-01"))
                .andExpect(jsonPath("$[1].vigencia.hasta").isEmpty())
                .andExpect(jsonPath("$[1].vigente").value(true));
    }

    @Test
    void c7PermiteFiltrarPorPlan() throws Exception {
        mockMvc.perform(get("/api/v1/equivalencias?planOrigen=1121&planDestino=1621"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(get("/api/v1/equivalencias?planOrigen=1621&planDestino=5010"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].resolucion").value("Res. 456/22"));

        mockMvc.perform(get("/api/v1/equivalencias?planOrigen=9999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void c8DevuelveElHistorialConLasRecursadas() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/historial").header("X-Legajo", LEGAJO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(8))
                .andExpect(jsonPath("$[0].materia.codigo").value("3.4.072"))
                .andExpect(jsonPath("$[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$[1].materia.codigo").value("3.4.075"))
                .andExpect(jsonPath("$[1].estado").value("FINAL"))
                .andExpect(jsonPath("$[1].cursadas.length()").value(2))
                .andExpect(jsonPath("$[1].cursadas[0].anio").value(2024))
                .andExpect(jsonPath("$[1].cursadas[0].cuatrimestre").value(1))
                .andExpect(jsonPath("$[1].cursadas[0].condicion").value("LIBRE"))
                .andExpect(jsonPath("$[1].cursadas[0].nota").value(3.0))
                .andExpect(jsonPath("$[1].cursadas[1].cuatrimestre").value(2))
                .andExpect(jsonPath("$[1].cursadas[1].condicion").value("REGULAR"))
                .andExpect(jsonPath("$[1].cursadas[1].nota").value(6.0))
                .andExpect(jsonPath("$[3].materia.codigo").value("3.4.207"))
                .andExpect(jsonPath("$[3].estado").value("CURSANDO"));
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
    void listaLosPlanesDestinoExcluyendoElOrigenElegido() throws Exception {
        mockMvc.perform(get("/api/v1/planes")
                        .header("X-Legajo", "1100002")
                        .param("excluir", "1621"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(6))
                .andExpect(jsonPath("$[0].codigoPlan").value("1121"))
                .andExpect(jsonPath("$[0].facultad").value("Facultad de Ingeniería y Ciencias Exactas"))
                .andExpect(jsonPath("$[1].codigoPlan").value("5010"))
                .andExpect(jsonPath("$[1].facultad").value("Facultad de Ciencias Económicas"))
                .andExpect(jsonPath("$[2].codigoPlan").value("5015"))
                .andExpect(jsonPath("$[3].codigoPlan").value("5020"))
                .andExpect(jsonPath("$[4].codigoPlan").value("5030"))
                .andExpect(jsonPath("$[4].carrera").value("Licenciatura en Finanzas"))
                .andExpect(jsonPath("$[5].codigoPlan").value("5040"))
                .andExpect(jsonPath("$[5].carrera").value("Licenciatura en Biotecnología"));
    }

    @Test
    void simulaElCambioDeFacultadConOrigenExplicito() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", "1100002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1621\",\"codigoPlanDestino\":\"5010\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planOrigen.codigo").value("1621"))
                .andExpect(jsonPath("$.planDestino.codigo").value("5010"))
                .andExpect(jsonPath("$.planDestino.facultad").value("Facultad de Ciencias Económicas"))
                .andExpect(jsonPath("$.items.length()").value(40))
                .andExpect(jsonPath("$.items[0].codigo").value("1.1.083"))
                .andExpect(jsonPath("$.items[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[0].reconocimiento.tipo").value("MISMA_MATERIA"))
                .andExpect(jsonPath("$.items[1].codigo").value("1.3.067"))
                .andExpect(jsonPath("$.items[1].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[11].codigo").value("1.3.068"))
                .andExpect(jsonPath("$.items[11].estado").value("CURSANDO"))
                .andExpect(jsonPath("$.items[25].codigo").value("1.1.144"))
                .andExpect(jsonPath("$.items[25].reconocimiento.tipo").value("EQUIVALENCIA"))
                .andExpect(jsonPath("$.items[25].reconocimiento.respaldo").value("Res. 456/22"))
                .andExpect(jsonPath("$.items[25].reconocimiento.materiasOrigen[0].codigo")
                        .value("3.4.164"))
                .andExpect(jsonPath("$.resumen.aprobadas").value(3))
                .andExpect(jsonPath("$.resumen.materiasReconocidas").value(4));
    }

    @Test
    void simulaLaActualizacionDePlanDeLaMismaCarrera() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", "1100002")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"5010\",\"codigoPlanDestino\":\"5015\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.planOrigen.codigo").value("5010"))
                .andExpect(jsonPath("$.planOrigen.carrera").value("Contador Público"))
                .andExpect(jsonPath("$.planDestino.codigo").value("5015"))
                .andExpect(jsonPath("$.planDestino.carrera").value("Contador Público"))
                .andExpect(jsonPath("$.items.length()").value(41))
                .andExpect(jsonPath("$.items[0].codigo").value("1.1.083"))
                .andExpect(jsonPath("$.items[0].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[1].codigo").value("1.3.067"))
                .andExpect(jsonPath("$.items[1].estado").value("APROBADA"))
                .andExpect(jsonPath("$.items[39].codigo").value("1.3.077"))
                .andExpect(jsonPath("$.items[39].estado").value("A_CURSAR"))
                .andExpect(jsonPath("$.resumen.aprobadas").value(2));
    }

    @Test
    void devuelve404SiElOrigenNoEstaActivoParaElLegajo() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1621\",\"codigoPlanDestino\":\"5010\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("El plan 1621 no está activo para el legajo " + LEGAJO));
    }

    @Test
    void devuelve404SiElPlanNoExiste() throws Exception {
        mockMvc.perform(get("/api/v1/planes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No existe el plan 9999"));
    }

    @Test
    void devuelve400SiElLegajoTieneFormatoInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/estudiantes/situacion").header("X-Legajo", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje")
                        .value("el legajo debe tener entre 7 y 15 dígitos"));

        mockMvc.perform(get("/api/v1/estudiantes/situacion").header("X-Legajo", "123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void devuelve400SiElCodigoDePlanEsInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1121\",\"codigoPlanDestino\":\"../etc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        mockMvc.perform(get("/api/v1/planes")
                        .header("X-Legajo", LEGAJO)
                        .param("excluir", "mal/codigo!"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void devuelve404ParaRutasInexistentes() throws Exception {
        mockMvc.perform(get("/api/v1/ruta/inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void devuelve409SiElPlanDestinoEsElActual() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1121\",\"codigoPlanDestino\":\"1121\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void devuelve400SiElBodyEsInvalido() throws Exception {
        mockMvc.perform(post("/api/v1/simulaciones")
                        .header("X-Legajo", LEGAJO)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPlanOrigen\":\"1121\",\"codigoPlanDestino\":\"\"}"))
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
