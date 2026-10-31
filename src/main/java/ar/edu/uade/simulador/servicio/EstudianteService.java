package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dto.MateriaEstadoDTO;
import ar.edu.uade.simulador.dto.SituacionAcademicaResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EstudianteService {

    private static final String SIN_CURSAR = "SIN_CURSAR";

    private final FuenteSituacionAcademica fuente;

    public SituacionAcademicaResponse obtenerSituacion(String legajo) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        PlanEstudio plan = fuente.obtenerPlanActual(legajo);
        Map<Integer, HistorialAcademico> historialPorMateria = fuente.obtenerHistorial(legajo).stream()
                .collect(Collectors.toMap(h -> h.getMateria().getIdMateria(), Function.identity()));

        List<MateriaEstadoDTO> materias = plan.getMaterias().stream()
                .sorted(PlanMateria.porUbicacion())
                .map(PlanMateria::getMateria)
                .map(materia -> toDto(materia, historialPorMateria.get(materia.getIdMateria())))
                .toList();

        return new SituacionAcademicaResponse(estudiante.getLegajo(), plan.getCodigoPlan(), materias);
    }

    private MateriaEstadoDTO toDto(Materia materia, HistorialAcademico historial) {
        String estado = historial == null ? SIN_CURSAR : historial.getEstadoGlobal().name();
        return new MateriaEstadoDTO(
                materia.getCodigo(),
                materia.getNombre(),
                estado,
                historial == null ? null : historial.getNotaFinal());
    }
}
