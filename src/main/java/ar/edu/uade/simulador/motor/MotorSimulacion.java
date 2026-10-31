package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanMateria;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class MotorSimulacion {

    private final List<ReglaEquivalencia> reglas;

    public MotorSimulacion(List<ReglaEquivalencia> reglas) {
        this.reglas = reglas;
    }

    public ResultadoSimulacion simular(ContextoSimulacion ctx) {
        Set<Integer> materiasOrigenUsadas = new HashSet<>();
        List<ItemSimulado> items = new ArrayList<>();

        for (PlanMateria destino : ctx.materiasDestino()) {
            ItemSimulado item = evaluarMateria(destino, ctx, materiasOrigenUsadas);
            if (item.reconocimiento() != null) {
                item.reconocimiento().materiasOrigen()
                        .forEach(materia -> materiasOrigenUsadas.add(materia.getIdMateria()));
            }
            items.add(item);
        }

        return new ResultadoSimulacion(
                List.copyOf(items),
                ResumenSimulacion.calcular(items),
                optativasSinCorrespondencia(ctx));
    }

    private ItemSimulado evaluarMateria(
            PlanMateria destino, ContextoSimulacion ctx, Set<Integer> materiasOrigenUsadas) {
        List<Reconocimiento> candidatos = reglas.stream()
                .map(regla -> regla.evaluar(destino, ctx))
                .flatMap(Optional::stream)
                .filter(reconocimiento -> reconocimiento.materiasOrigen().stream()
                        .noneMatch(materia -> materiasOrigenUsadas.contains(materia.getIdMateria())))
                .toList();

        return elegirMejor(candidatos)
                .map(reconocimiento -> ItemSimulado.reconocido(destino, reconocimiento))
                .orElseGet(() -> ItemSimulado.aCursar(destino));
    }

    private Optional<Reconocimiento> elegirMejor(List<Reconocimiento> candidatos) {
        return candidatos.stream()
                .min(Comparator.comparingInt(reconocimiento -> reconocimiento.estado().ordinal()));
    }

    private List<Materia> optativasSinCorrespondencia(ContextoSimulacion ctx) {
        return ctx.optativasAprobadasEnPlanOrigen().stream()
                .filter(materia -> !ctx.tieneDestinoConMismaMateria(materia))
                .filter(materia -> !ctx.tieneEquivalenciaComoOrigen(materia))
                .toList();
    }
}
