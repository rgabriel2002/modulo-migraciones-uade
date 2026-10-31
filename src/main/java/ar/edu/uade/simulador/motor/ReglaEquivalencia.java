package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.PlanMateria;
import java.util.Optional;

public interface ReglaEquivalencia {

    Optional<Reconocimiento> evaluar(PlanMateria destino, ContextoSimulacion ctx);
}
