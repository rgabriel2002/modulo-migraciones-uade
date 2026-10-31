package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanMateria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReglaMismaMateria extends ReglaEquivalenciaBase {

    @Override
    protected boolean esAplicable(PlanMateria destino, ContextoSimulacion ctx) {
        Materia materia = destino.getMateria();
        if (ctx.estadoDe(materia).isEmpty()) {
            return false;
        }
        Optional<Integer> horasOrigen = ctx.horasEnPlanOrigen(materia);
        if (horasOrigen.isEmpty() && materia.getCargaHorariaTotal() == null) {
            return false;
        }
        int horas = horasOrigen.orElse(materia.getCargaHorariaTotal());
        return horas == destino.getCargaHoraria();
    }

    @Override
    protected Reconocimiento construir(PlanMateria destino, ContextoSimulacion ctx) {
        Materia materia = destino.getMateria();
        EstadoSimulado estado = EstadoSimulado.desde(ctx.estadoDe(materia).orElseThrow());
        return new Reconocimiento(estado, "Misma materia", List.of(materia));
    }
}
