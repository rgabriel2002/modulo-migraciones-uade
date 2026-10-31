package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.DetalleCursada;
import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dto.DetalleCursadaDTO;
import ar.edu.uade.simulador.dto.HistorialMateriaDTO;
import ar.edu.uade.simulador.dto.MateriaEstadoDTO;
import ar.edu.uade.simulador.dto.MateriaRefDTO;
import ar.edu.uade.simulador.dto.PlanRefDTO;
import ar.edu.uade.simulador.dto.SituacionAcademicaResponse;
import ar.edu.uade.simulador.repositorio.HistorialAcademicoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final HistorialAcademicoRepository historialRepository;

    public SituacionAcademicaResponse obtenerSituacion(String legajo) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        PlanEstudio plan = fuente.obtenerPlanActual(legajo);
        Map<Integer, HistorialAcademico> historialPorMateria = fuente.obtenerHistorial(legajo).stream()
                .collect(Collectors.toMap(h -> h.getMateria().getIdMateria(), Function.identity()));

        List<MateriaEstadoDTO> materias = plan.getMaterias().stream()
                .sorted(PlanMateria.porUbicacion())
                .map(planMateria -> toDto(
                        planMateria, historialPorMateria.get(planMateria.getMateria().getIdMateria())))
                .toList();

        PlanRefDTO planRef = new PlanRefDTO(plan.getCodigoPlan(), plan.getCarrera().getNombre());
        return new SituacionAcademicaResponse(estudiante.getLegajo(), planRef, materias);
    }

    public List<HistorialMateriaDTO> obtenerHistorial(String legajo) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        return historialRepository.buscarConCursadas(estudiante).stream()
                .sorted(Comparator.comparing(h -> h.getMateria().getCodigo()))
                .map(this::toHistorialDto)
                .toList();
    }

    private MateriaEstadoDTO toDto(PlanMateria planMateria, HistorialAcademico historial) {
        Materia materia = planMateria.getMateria();
        String estado = historial == null ? SIN_CURSAR : historial.getEstadoGlobal().name();
        return new MateriaEstadoDTO(
                materia.getCodigo(),
                materia.getNombre(),
                planMateria.getAnioPlan(),
                planMateria.getCuatrimestre(),
                estado,
                historial == null ? null : historial.getNotaFinal(),
                historial != null);
    }

    private HistorialMateriaDTO toHistorialDto(HistorialAcademico historial) {
        List<DetalleCursadaDTO> cursadas = historial.getCursadas().stream()
                .sorted(Comparator.comparing(DetalleCursada::getAnioCursada)
                        .thenComparing(c -> Optional.ofNullable(c.getCuatrimestre()).orElse((short) 0)))
                .map(c -> new DetalleCursadaDTO(
                        c.getAnioCursada(),
                        c.getCuatrimestre(),
                        c.getCondicionAlcanzada() == null ? null : c.getCondicionAlcanzada().name(),
                        c.getNotaCierreCursada()))
                .toList();

        return new HistorialMateriaDTO(
                new MateriaRefDTO(historial.getMateria().getCodigo(), historial.getMateria().getNombre()),
                historial.getEstadoGlobal().name(),
                historial.getNotaFinal(),
                historial.getFechaAprobacion(),
                cursadas);
    }
}
