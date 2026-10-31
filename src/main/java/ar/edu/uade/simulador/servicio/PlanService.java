package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoPlan;
import ar.edu.uade.simulador.dto.MateriaPlanDTO;
import ar.edu.uade.simulador.dto.PlanDetalleResponse;
import ar.edu.uade.simulador.dto.PlanResponse;
import ar.edu.uade.simulador.errores.RecursoNoEncontradoException;
import ar.edu.uade.simulador.repositorio.PlanEstudioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanService {

    private final FuenteSituacionAcademica fuente;
    private final PlanEstudioRepository planRepository;

    public List<PlanResponse> listarPlanesDestino(String legajo) {
        PlanEstudio actual = fuente.obtenerPlanActual(legajo);
        return planRepository.findByEstado(EstadoPlan.ACTIVO).stream()
                .filter(plan -> !plan.getCodigoPlan().equals(actual.getCodigoPlan()))
                .map(plan -> new PlanResponse(
                        plan.getCodigoPlan(),
                        plan.getCarrera().getNombre(),
                        plan.getEstado().name()))
                .toList();
    }

    public PlanDetalleResponse obtenerPlan(String codigo) {
        PlanEstudio plan = planRepository.buscarDetalle(codigo)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el plan " + codigo));

        List<MateriaPlanDTO> materias = plan.getMaterias().stream()
                .sorted(PlanMateria.porUbicacion())
                .map(this::toDto)
                .toList();

        return new PlanDetalleResponse(plan.getCodigoPlan(), materias);
    }

    private MateriaPlanDTO toDto(PlanMateria planMateria) {
        return new MateriaPlanDTO(
                planMateria.getMateria().getCodigo(),
                planMateria.getMateria().getNombre(),
                planMateria.getAnioPlan(),
                planMateria.getCuatrimestre(),
                planMateria.getCargaHoraria(),
                planMateria.isEsOptativa(),
                planMateria.isEsTituloIntermedio());
    }
}
