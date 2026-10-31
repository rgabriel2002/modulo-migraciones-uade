package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import ar.edu.uade.simulador.errores.MigracionNoPermitidaException;
import ar.edu.uade.simulador.errores.RecursoNoEncontradoException;
import ar.edu.uade.simulador.motor.ContextoSimulacion;
import ar.edu.uade.simulador.motor.MotorSimulacion;
import ar.edu.uade.simulador.repositorio.EquivalenciaRepository;
import ar.edu.uade.simulador.repositorio.PlanEstudioRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SimulacionService {

    private final FuenteSituacionAcademica fuente;
    private final PlanEstudioRepository planRepository;
    private final EquivalenciaRepository equivalenciaRepository;
    private final MotorSimulacion motor;
    private final SimulacionMapper mapper;

    public SimulacionResponse simular(String legajo, String codigoPlanDestino) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        PlanEstudio origen = fuente.obtenerPlanActual(legajo);
        PlanEstudio destino = planRepository.buscarDetalle(codigoPlanDestino)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el plan " + codigoPlanDestino));

        validarMigracion(origen, destino);

        List<HistorialAcademico> historial = fuente.obtenerHistorial(legajo);
        List<Equivalencia> equivalencias = equivalenciaRepository.findVigentes(
                origen, destino, LocalDate.now());

        ContextoSimulacion contexto = new ContextoSimulacion(
                estudiante, origen, destino, equivalencias, historial);

        return mapper.toResponse(motor.simular(contexto));
    }

    private void validarMigracion(PlanEstudio origen, PlanEstudio destino) {
        if (origen.getIdPlan().equals(destino.getIdPlan())) {
            throw new MigracionNoPermitidaException(
                    "El plan destino es el mismo que el plan actual del estudiante");
        }
        if (!destino.estaActivo()) {
            throw new MigracionNoPermitidaException(
                    "El plan " + destino.getCodigoPlan() + " no está activo");
        }
    }
}
