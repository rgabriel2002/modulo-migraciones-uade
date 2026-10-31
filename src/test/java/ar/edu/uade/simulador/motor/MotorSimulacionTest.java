package ar.edu.uade.simulador.motor;

import static org.assertj.core.api.Assertions.assertThat;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.EquivalenciaMateria;
import ar.edu.uade.simulador.dominio.EquivalenciaMateriaId;
import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.PlanMateriaId;
import ar.edu.uade.simulador.dominio.enums.EstadoMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoPlan;
import ar.edu.uade.simulador.dominio.enums.RolEquivalencia;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MotorSimulacionTest {

    private MotorSimulacion motor;
    private Estudiante estudiante;

    private PlanEstudio plan1121;
    private PlanEstudio plan1621;
    private Materia fundamentos;
    private Materia sistemas;
    private Materia algoritmia;
    private Materia discreta;
    private Materia baseDeDatos;
    private Materia programacionII;
    private Materia taller;
    private Materia seminario;
    private Materia programacionI;
    private Equivalencia equivalenciaAlgoritmia;

    @BeforeEach
    void setUp() {
        motor = new MotorSimulacion(List.of(new ReglaMismaMateria(), new ReglaEquivalenciaPorResolucion()));

        estudiante = new Estudiante();
        estudiante.setNombre("Alumno");
        estudiante.setApellido("De Prueba");

        fundamentos = materia(1, "3.4.069", 68);
        sistemas = materia(2, "3.4.164", 68);
        algoritmia = materia(3, "3.4.225", 68);
        discreta = materia(4, "9.9.001", 102);
        baseDeDatos = materia(5, "9.9.002", 68);
        programacionII = materia(6, "9.9.003", 68);
        taller = materia(7, "9.9.004", 68);
        seminario = materia(8, "9.9.005", 68);
        programacionI = materia(9, "9.9.006", 68);

        plan1121 = plan(1, "1121");
        planMateria(plan1121, algoritmia, 1, 1, 68, false, false);
        planMateria(plan1121, sistemas, 1, 1, 68, false, false);
        planMateria(plan1121, discreta, 1, 2, 68, false, false);
        planMateria(plan1121, programacionI, 1, 2, 68, false, false);
        planMateria(plan1121, baseDeDatos, 2, 1, 68, false, false);
        planMateria(plan1121, taller, 2, 2, 68, true, false);
        planMateria(plan1121, seminario, 2, 2, 68, true, false);

        plan1621 = plan(2, "1621");
        planMateria(plan1621, fundamentos, 1, 1, 68, false, true);
        planMateria(plan1621, sistemas, 1, 1, 68, false, true);
        planMateria(plan1621, discreta, 1, 2, 102, false, true);
        planMateria(plan1621, programacionI, 1, 2, 68, false, true);
        planMateria(plan1621, baseDeDatos, 2, 1, 68, false, true);
        planMateria(plan1621, programacionII, 2, 1, 68, false, true);
        planMateria(plan1621, taller, 2, 2, 68, true, false);

        equivalenciaAlgoritmia = equivalencia(
                1, plan1121, plan1621, "Res. 123/21", algoritmia, fundamentos);
    }

    @Test
    void simulaLaMigracionDeLaTecnicaturaAIngenieria() {
        List<HistorialAcademico> historial = List.of(
                historial(algoritmia, EstadoMateria.APROBADA),
                historial(sistemas, EstadoMateria.APROBADA),
                historial(discreta, EstadoMateria.APROBADA),
                historial(baseDeDatos, EstadoMateria.FINAL),
                historial(programacionI, EstadoMateria.CURSANDO),
                historial(taller, EstadoMateria.APROBADA),
                historial(seminario, EstadoMateria.APROBADA));

        ResultadoSimulacion resultado = simular(List.of(equivalenciaAlgoritmia), historial);
        Map<String, ItemSimulado> items = porCodigo(resultado);

        assertThat(items.get("3.4.069").estado()).isEqualTo(EstadoSimulado.APROBADA);
        assertThat(items.get("3.4.069").reconocimiento().respaldo()).isEqualTo("Res. 123/21");
        assertThat(items.get("3.4.164").estado()).isEqualTo(EstadoSimulado.APROBADA);
        assertThat(items.get("3.4.164").reconocimiento().respaldo()).isEqualTo("Misma materia");
        assertThat(items.get("9.9.002").estado()).isEqualTo(EstadoSimulado.FINAL);
        assertThat(items.get("9.9.006").estado()).isEqualTo(EstadoSimulado.CURSANDO);
        assertThat(items.get("9.9.001").estado()).isEqualTo(EstadoSimulado.A_CURSAR);
        assertThat(items.get("9.9.003").estado()).isEqualTo(EstadoSimulado.A_CURSAR);
        assertThat(items.get("9.9.004").estado()).isEqualTo(EstadoSimulado.APROBADA);

        ResumenSimulacion resumen = resultado.resumen();
        assertThat(resumen.aprobadas()).isEqualTo(3);
        assertThat(resumen.pendientesDeFinal()).isEqualTo(1);
        assertThat(resumen.pendientesDeCursado()).isEqualTo(3);
        assertThat(resumen.materiasReconocidas()).isEqualTo(5);
        assertThat(resumen.avanceTituloIntermedio()).isEqualTo(33.33);

        assertThat(resultado.optativasSinCorrespondencia())
                .extracting(Materia::getCodigo)
                .containsExactly("9.9.005");
    }

    @Test
    void noReconoceUnaMateriaSiLaCargaHorariaNoCoincide() {
        List<HistorialAcademico> historial = List.of(historial(discreta, EstadoMateria.APROBADA));

        ResultadoSimulacion resultado = simular(List.of(), historial);

        assertThat(porCodigo(resultado).get("9.9.001").estado()).isEqualTo(EstadoSimulado.A_CURSAR);
        assertThat(porCodigo(resultado).get("9.9.001").estaReconocida()).isFalse();
    }

    @Test
    void usaCadaMateriaDeOrigenUnaSolaVez() {
        PlanEstudio origen = plan(10, "1000");
        PlanEstudio destino = plan(11, "2000");
        Materia origenUnica = materia(100, "9.8.001", 68);
        Materia destinoA = materia(101, "9.8.101", 68);
        Materia destinoB = materia(102, "9.8.102", 68);

        planMateria(origen, origenUnica, 1, 1, 68, false, false);
        planMateria(destino, destinoA, 1, 1, 68, false, false);
        planMateria(destino, destinoB, 2, 1, 68, false, false);

        List<Equivalencia> equivalencias = List.of(
                equivalencia(2, origen, destino, "Res. 1/24", origenUnica, destinoA),
                equivalencia(3, origen, destino, "Res. 2/24", origenUnica, destinoB));

        ContextoSimulacion contexto = new ContextoSimulacion(
                estudiante, origen, destino, equivalencias,
                List.of(historial(origenUnica, EstadoMateria.APROBADA)));

        ResultadoSimulacion resultado = motor.simular(contexto);
        Map<String, ItemSimulado> items = porCodigo(resultado);

        assertThat(items.get("9.8.101").estado()).isEqualTo(EstadoSimulado.APROBADA);
        assertThat(items.get("9.8.102").estado()).isEqualTo(EstadoSimulado.A_CURSAR);
        assertThat(resultado.resumen().materiasReconocidas()).isEqualTo(1);
    }

    @Test
    void heredaElPeorEstadoDeLasMateriasDeOrigen() {
        PlanEstudio origen = plan(20, "3000");
        PlanEstudio destino = plan(21, "4000");
        Materia aprobada = materia(200, "9.7.001", 68);
        Materia cursando = materia(201, "9.7.002", 68);
        Materia destinoMateria = materia(202, "9.7.100", 136);

        planMateria(origen, aprobada, 1, 1, 68, false, false);
        planMateria(origen, cursando, 1, 2, 68, false, false);
        planMateria(destino, destinoMateria, 1, 1, 136, false, false);

        Equivalencia equivalencia = equivalencia(
                4, origen, destino, "Res. 3/24", aprobada, destinoMateria);
        equivalencia.getMaterias().add(equivalenciaMateria(equivalencia, cursando, RolEquivalencia.ORIGEN));

        ContextoSimulacion contexto = new ContextoSimulacion(
                estudiante, origen, destino, List.of(equivalencia),
                List.of(historial(aprobada, EstadoMateria.APROBADA),
                        historial(cursando, EstadoMateria.CURSANDO)));

        ResultadoSimulacion resultado = motor.simular(contexto);

        assertThat(porCodigo(resultado).get("9.7.100").estado()).isEqualTo(EstadoSimulado.CURSANDO);
    }

    @Test
    void noReconocePorEquivalenciaSiFaltaUnaMateriaDeOrigen() {
        PlanEstudio origen = plan(30, "5000");
        PlanEstudio destino = plan(31, "6000");
        Materia aprobada = materia(300, "9.6.001", 68);
        Materia sinCursar = materia(301, "9.6.002", 68);
        Materia destinoMateria = materia(302, "9.6.100", 136);

        planMateria(origen, aprobada, 1, 1, 68, false, false);
        planMateria(origen, sinCursar, 1, 2, 68, false, false);
        planMateria(destino, destinoMateria, 1, 1, 136, false, false);

        Equivalencia equivalencia = equivalencia(
                5, origen, destino, "Res. 4/24", aprobada, destinoMateria);
        equivalencia.getMaterias().add(equivalenciaMateria(equivalencia, sinCursar, RolEquivalencia.ORIGEN));

        ContextoSimulacion contexto = new ContextoSimulacion(
                estudiante, origen, destino, List.of(equivalencia),
                List.of(historial(aprobada, EstadoMateria.APROBADA)));

        ResultadoSimulacion resultado = motor.simular(contexto);

        assertThat(porCodigo(resultado).get("9.6.100").estado()).isEqualTo(EstadoSimulado.A_CURSAR);
        assertThat(resultado.resumen().materiasReconocidas()).isZero();
    }

    private ResultadoSimulacion simular(List<Equivalencia> equivalencias, List<HistorialAcademico> historial) {
        return motor.simular(new ContextoSimulacion(
                estudiante, plan1121, plan1621, equivalencias, historial));
    }

    private Map<String, ItemSimulado> porCodigo(ResultadoSimulacion resultado) {
        return resultado.items().stream()
                .collect(Collectors.toMap(
                        item -> item.materiaDestino().getMateria().getCodigo(),
                        item -> item));
    }

    private static PlanEstudio plan(int id, String codigo) {
        PlanEstudio plan = new PlanEstudio();
        plan.setIdPlan(id);
        plan.setCodigoPlan(codigo);
        plan.setEstado(EstadoPlan.ACTIVO);
        return plan;
    }

    private static Materia materia(int id, String codigo, int cargaHoraria) {
        Materia materia = new Materia();
        materia.setIdMateria(id);
        materia.setCodigo(codigo);
        materia.setNombre("Materia " + codigo);
        materia.setCargaHorariaTotal(cargaHoraria);
        return materia;
    }

    private static void planMateria(
            PlanEstudio plan,
            Materia materia,
            int anio,
            int cuatrimestre,
            int cargaHoraria,
            boolean optativa,
            boolean tituloIntermedio) {
        PlanMateria planMateria = new PlanMateria();
        planMateria.setId(new PlanMateriaId(plan.getIdPlan(), materia.getIdMateria()));
        planMateria.setPlan(plan);
        planMateria.setMateria(materia);
        planMateria.setAnioPlan((short) anio);
        planMateria.setCuatrimestre((short) cuatrimestre);
        planMateria.setCargaHoraria(cargaHoraria);
        planMateria.setEsOptativa(optativa);
        planMateria.setEsTituloIntermedio(tituloIntermedio);
        plan.getMaterias().add(planMateria);
    }

    private HistorialAcademico historial(Materia materia, EstadoMateria estado) {
        HistorialAcademico historial = new HistorialAcademico();
        historial.setEstudiante(estudiante);
        historial.setMateria(materia);
        historial.setEstadoGlobal(estado);
        return historial;
    }

    private static Equivalencia equivalencia(
            int id,
            PlanEstudio origen,
            PlanEstudio destino,
            String resolucion,
            Materia materiaOrigen,
            Materia materiaDestino) {
        Equivalencia equivalencia = new Equivalencia();
        equivalencia.setIdEquivalencia(id);
        equivalencia.setPlanOrigen(origen);
        equivalencia.setPlanDestino(destino);
        equivalencia.setResolucion(resolucion);
        equivalencia.setVigenteDesde(LocalDate.of(2021, 3, 1));
        equivalencia.getMaterias().add(equivalenciaMateria(equivalencia, materiaOrigen, RolEquivalencia.ORIGEN));
        equivalencia.getMaterias().add(equivalenciaMateria(equivalencia, materiaDestino, RolEquivalencia.DESTINO));
        return equivalencia;
    }

    private static EquivalenciaMateria equivalenciaMateria(
            Equivalencia equivalencia, Materia materia, RolEquivalencia rol) {
        EquivalenciaMateria equivalenciaMateria = new EquivalenciaMateria();
        equivalenciaMateria.setId(new EquivalenciaMateriaId(
                equivalencia.getIdEquivalencia(), materia.getIdMateria(), rol));
        equivalenciaMateria.setEquivalencia(equivalencia);
        equivalenciaMateria.setMateria(materia);
        return equivalenciaMateria;
    }
}
