package ar.edu.uade.simulador.integracion;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.EstudiantePlan;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.enums.EstadoInscripcion;
import ar.edu.uade.simulador.errores.RecursoNoEncontradoException;
import ar.edu.uade.simulador.repositorio.EstudiantePlanRepository;
import ar.edu.uade.simulador.repositorio.EstudianteRepository;
import ar.edu.uade.simulador.repositorio.HistorialAcademicoRepository;
import ar.edu.uade.simulador.servicio.FuenteSituacionAcademica;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuenteSituacionAcademicaLocal implements FuenteSituacionAcademica {

    private final EstudianteRepository estudianteRepository;
    private final EstudiantePlanRepository estudiantePlanRepository;
    private final HistorialAcademicoRepository historialRepository;

    @Override
    public Estudiante obtenerEstudiante(String legajo) {
        return estudianteRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el estudiante con legajo " + legajo));
    }

    @Override
    public List<EstudiantePlan> obtenerPlanesActivos(String legajo) {
        Estudiante estudiante = obtenerEstudiante(legajo);
        return estudiantePlanRepository.buscarPorEstudianteYEstado(estudiante, EstadoInscripcion.ACTIVO);
    }

    @Override
    public PlanEstudio obtenerPlanActivo(String legajo, String codigoPlan) {
        Estudiante estudiante = obtenerEstudiante(legajo);
        return estudiantePlanRepository
                .buscarPorEstudianteEstadoYPlan(estudiante, EstadoInscripcion.ACTIVO, codigoPlan)
                .map(EstudiantePlan::getPlan)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El plan " + codigoPlan + " no está activo para el legajo " + legajo));
    }

    @Override
    public List<HistorialAcademico> obtenerHistorial(String legajo) {
        return historialRepository.findByEstudiante(obtenerEstudiante(legajo));
    }
}
