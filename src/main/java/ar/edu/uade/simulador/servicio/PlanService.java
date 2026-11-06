package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Correlatividad;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoPlan;
import ar.edu.uade.simulador.dto.CorrelatividadDTO;
import ar.edu.uade.simulador.dto.MateriaPlanDTO;
import ar.edu.uade.simulador.dto.PlanDetalleResponse;
import ar.edu.uade.simulador.dto.PlanResponse;
import ar.edu.uade.simulador.errores.RecursoNoEncontradoException;
import ar.edu.uade.simulador.repositorio.CorrelatividadRepository;
import ar.edu.uade.simulador.repositorio.PlanEstudioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final FuenteSituacionAcademica fuente;
    private final PlanEstudioRepository planRepository;
    private final CorrelatividadRepository correlatividadRepository;

    public List<PlanResponse> listarPlanesDestino(String legajo, String codigoPlanExcluir) {
        fuente.obtenerEstudiante(legajo);
        boolean excluir = codigoPlanExcluir != null && !codigoPlanExcluir.isBlank();
        return planRepository.findByEstado(EstadoPlan.ACTIVO).stream()
                .filter(plan -> !excluir || !plan.getCodigoPlan().equals(codigoPlanExcluir))
                .sorted(Comparator.comparing(PlanEstudio::getCodigoPlan))
                .map(plan -> new PlanResponse(
                        plan.getCodigoPlan(),
                        plan.getCarrera().getNombre(),
                        plan.getCarrera().getFacultad().getNombre(),
                        plan.getEstado().name()))
                .toList();
    }

    public PlanDetalleResponse obtenerPlan(String codigo) {
        PlanEstudio plan = planRepository.buscarDetalle(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el plan " + codigo));

        Map<Integer, List<Correlatividad>> correlatividades = correlatividadRepository.findByPlan(plan)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getMateria().getIdMateria()));

        List<MateriaPlanDTO> materias = plan.getMaterias().stream()
                .sorted(PlanMateria.porUbicacion())
                .map(planMateria -> toDto(
                        planMateria,
                        correlatividades.getOrDefault(planMateria.getMateria().getIdMateria(), List.of())))
                .toList();

        return new PlanDetalleResponse(plan.getCodigoPlan(), plan.getCarrera().getNombre(), materias);
    }

    private MateriaPlanDTO toDto(PlanMateria planMateria, List<Correlatividad> correlatividades) {
        List<CorrelatividadDTO> correlativas = correlatividades.stream()
                .sorted(Comparator.comparing(c -> c.getMateriaRequerida().getCodigo()))
                .map(c -> new CorrelatividadDTO(
                        c.getMateriaRequerida().getCodigo(),
                        c.getMateriaRequerida().getNombre(),
                        c.getTipo().name()))
                .toList();

        return new MateriaPlanDTO(
                planMateria.getMateria().getCodigo(),
                planMateria.getMateria().getNombre(),
                planMateria.getAnioPlan(),
                planMateria.getCuatrimestre(),
                planMateria.getCargaHoraria(),
                planMateria.isEsOptativa(),
                planMateria.isEsTituloIntermedio(),
                correlativas);
    }
}
