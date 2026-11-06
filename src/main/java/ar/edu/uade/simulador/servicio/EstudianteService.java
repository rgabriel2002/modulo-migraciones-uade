package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.DetalleCursada;
import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.EstudiantePlan;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dto.DetalleCursadaDTO;
import ar.edu.uade.simulador.dto.HistorialMateriaDTO;
import ar.edu.uade.simulador.dto.MateriaRefDTO;
import ar.edu.uade.simulador.dto.PlanRefDTO;
import ar.edu.uade.simulador.dto.SituacionAcademicaResponse;
import ar.edu.uade.simulador.repositorio.HistorialAcademicoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EstudianteService {

    private final FuenteSituacionAcademica fuente;
    private final HistorialAcademicoRepository historialRepository;

    public SituacionAcademicaResponse obtenerSituacion(String legajo) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        List<PlanRefDTO> planesActivos = fuente.obtenerPlanesActivos(legajo).stream()
                .map(EstudiantePlan::getPlan)
                .sorted(Comparator.comparing(PlanEstudio::getCodigoPlan))
                .map(this::toRef)
                .toList();

        return new SituacionAcademicaResponse(estudiante.getLegajo(), planesActivos);
    }

    public List<HistorialMateriaDTO> obtenerHistorial(String legajo) {
        Estudiante estudiante = fuente.obtenerEstudiante(legajo);
        return historialRepository.buscarConCursadas(estudiante).stream()
                .sorted(Comparator.comparing(h -> h.getMateria().getCodigo()))
                .map(this::toHistorialDto)
                .toList();
    }

    private PlanRefDTO toRef(PlanEstudio plan) {
        return new PlanRefDTO(
                plan.getCodigoPlan(),
                plan.getCarrera().getNombre(),
                plan.getCarrera().getFacultad().getNombre());
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
