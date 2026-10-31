package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dto.MateriaRefDTO;
import ar.edu.uade.simulador.dto.ReconocimientoDTO;
import ar.edu.uade.simulador.dto.ResumenDTO;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import ar.edu.uade.simulador.dto.ItemSimuladoDTO;
import ar.edu.uade.simulador.dto.PlanRefDTO;
import ar.edu.uade.simulador.dto.TituloIntermedioDTO;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.motor.ItemSimulado;
import ar.edu.uade.simulador.motor.Reconocimiento;
import ar.edu.uade.simulador.motor.ResultadoSimulacion;
import ar.edu.uade.simulador.motor.ResumenSimulacion;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SimulacionMapper {

    public SimulacionResponse toResponse(
            ResultadoSimulacion resultado, PlanEstudio planOrigen, PlanEstudio planDestino) {
        List<ItemSimuladoDTO> items = resultado.items().stream()
                .map(this::toItem)
                .toList();

        ResumenSimulacion resumen = resultado.resumen();
        ResumenDTO resumenDto = new ResumenDTO(
                resumen.aprobadas(),
                resumen.pendientesDeFinal(),
                resumen.pendientesDeCursado(),
                resumen.materiasReconocidas(),
                resumen.pendientesPlanActual(),
                resumen.pendientesPlanNuevo(),
                new TituloIntermedioDTO(
                        resumen.aprobadasTituloIntermedio(),
                        resumen.totalTituloIntermedio(),
                        resumen.avanceTituloIntermedio()));

        List<MateriaRefDTO> optativas = resultado.optativasSinCorrespondencia().stream()
                .map(this::toRef)
                .toList();

        return new SimulacionResponse(
                toRef(planOrigen), toRef(planDestino), items, resumenDto, optativas);
    }

    private ItemSimuladoDTO toItem(ItemSimulado item) {
        PlanMateria destino = item.materiaDestino();
        return new ItemSimuladoDTO(
                destino.getMateria().getCodigo(),
                destino.getMateria().getNombre(),
                destino.getAnioPlan(),
                destino.getCuatrimestre(),
                destino.getCargaHoraria(),
                destino.isEsOptativa(),
                destino.isEsTituloIntermedio(),
                item.estado().name(),
                toReconocimiento(item.reconocimiento()));
    }

    private ReconocimientoDTO toReconocimiento(Reconocimiento reconocimiento) {
        if (reconocimiento == null) {
            return null;
        }
        return new ReconocimientoDTO(
                reconocimiento.tipo().name(),
                reconocimiento.respaldo(),
                reconocimiento.materiasOrigen().stream().map(this::toRef).toList());
    }

    private PlanRefDTO toRef(PlanEstudio plan) {
        return new PlanRefDTO(plan.getCodigoPlan(), plan.getCarrera().getNombre());
    }

    private MateriaRefDTO toRef(Materia materia) {
        return new MateriaRefDTO(materia.getCodigo(), materia.getNombre());
    }
}
