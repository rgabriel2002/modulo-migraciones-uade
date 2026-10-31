package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoMateria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class ReglaEquivalenciaPorResolucion extends ReglaEquivalenciaBase {

    @Override
    protected boolean esAplicable(PlanMateria destino, ContextoSimulacion ctx) {
        return equivalenciaAplicable(destino, ctx).isPresent();
    }

    @Override
    protected Reconocimiento construir(PlanMateria destino, ContextoSimulacion ctx) {
        Equivalencia equivalencia = equivalenciaAplicable(destino, ctx).orElseThrow();
        List<EstadoMateria> estados = equivalencia.getMateriasOrigen().stream()
                .map(materia -> ctx.estadoDe(materia).orElseThrow())
                .toList();
        return new Reconocimiento(
                estadoHeredado(estados),
                TipoReconocimiento.EQUIVALENCIA,
                equivalencia.getResolucion(),
                equivalencia.getMateriasOrigen());
    }

    private int sumaHorasOrigen(Equivalencia equivalencia, ContextoSimulacion ctx) {
        return equivalencia.getMateriasOrigen().stream()
                .mapToInt(materia -> ctx.horasEnPlanOrigen(materia)
                        .orElseGet(() -> Optional.ofNullable(materia.getCargaHorariaTotal()).orElse(0)))
                .sum();
    }

    private Optional<Equivalencia> equivalenciaAplicable(PlanMateria destino, ContextoSimulacion ctx) {
        return ctx.equivalenciasHacia(destino.getMateria()).stream()
                .filter(equivalencia -> !equivalencia.getMateriasOrigen().isEmpty())
                .filter(equivalencia -> equivalencia.getMateriasOrigen().stream()
                        .allMatch(materia -> ctx.estadoDe(materia).isPresent()))
                .filter(equivalencia -> sumaHorasOrigen(equivalencia, ctx) == destino.getCargaHoraria())
                .findFirst();
    }
}
