package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoMateria;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public abstract class ReglaEquivalenciaBase implements ReglaEquivalencia {

    @Override
    public Optional<Reconocimiento> evaluar(PlanMateria destino, ContextoSimulacion ctx) {
        if (!esAplicable(destino, ctx)) {
            return Optional.empty();
        }
        return Optional.of(construir(destino, ctx));
    }

    protected abstract boolean esAplicable(PlanMateria destino, ContextoSimulacion ctx);

    protected abstract Reconocimiento construir(PlanMateria destino, ContextoSimulacion ctx);

    protected EstadoSimulado estadoHeredado(List<EstadoMateria> estados) {
        return estados.stream()
                .min(Comparator.comparingInt(EstadoMateria::rango))
                .map(EstadoSimulado::desde)
                .orElse(EstadoSimulado.A_CURSAR);
    }
}
