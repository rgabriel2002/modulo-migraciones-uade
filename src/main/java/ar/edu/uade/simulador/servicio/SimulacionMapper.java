package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dto.ItemSimuladoDTO;
import ar.edu.uade.simulador.dto.ResumenDTO;
import ar.edu.uade.simulador.dto.SimulacionResponse;
import ar.edu.uade.simulador.motor.ItemSimulado;
import ar.edu.uade.simulador.motor.Reconocimiento;
import ar.edu.uade.simulador.motor.ResultadoSimulacion;
import ar.edu.uade.simulador.motor.ResumenSimulacion;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SimulacionMapper {

    public SimulacionResponse toResponse(ResultadoSimulacion resultado) {
        List<ItemSimuladoDTO> items = resultado.items().stream()
                .map(this::toItem)
                .toList();

        ResumenSimulacion resumen = resultado.resumen();
        ResumenDTO resumenDto = new ResumenDTO(
                resumen.aprobadas(),
                resumen.pendientesDeFinal(),
                resumen.pendientesDeCursado(),
                resumen.materiasReconocidas(),
                resumen.avanceTituloIntermedio());

        List<String> optativas = resultado.optativasSinCorrespondencia().stream()
                .map(materia -> materia.getCodigo() + " " + materia.getNombre())
                .toList();

        return new SimulacionResponse(items, resumenDto, optativas);
    }

    private ItemSimuladoDTO toItem(ItemSimulado item) {
        PlanMateria destino = item.materiaDestino();
        Reconocimiento reconocimiento = item.reconocimiento();

        return new ItemSimuladoDTO(
                destino.getMateria().getCodigo(),
                destino.getMateria().getNombre(),
                destino.getAnioPlan(),
                destino.getCuatrimestre(),
                destino.getCargaHoraria(),
                destino.isEsOptativa(),
                destino.isEsTituloIntermedio(),
                item.estado().name(),
                reconocimiento == null ? null : reconocimiento.respaldo(),
                reconocimiento == null
                        ? List.of()
                        : reconocimiento.materiasOrigen().stream()
                                .map(materia -> materia.getCodigo() + " " + materia.getNombre())
                                .toList());
    }
}
