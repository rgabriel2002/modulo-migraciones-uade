package ar.edu.uade.simulador.motor;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.enums.EstadoMateria;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ContextoSimulacion {

    private final Estudiante estudiante;
    private final PlanEstudio planOrigen;
    private final PlanEstudio planDestino;
    private final List<Equivalencia> equivalencias;
    private final List<HistorialAcademico> historial;

    public ContextoSimulacion(
            Estudiante estudiante,
            PlanEstudio planOrigen,
            PlanEstudio planDestino,
            List<Equivalencia> equivalencias,
            List<HistorialAcademico> historial) {
        this.estudiante = estudiante;
        this.planOrigen = planOrigen;
        this.planDestino = planDestino;
        this.equivalencias = List.copyOf(equivalencias);
        this.historial = List.copyOf(historial);
    }

    public List<PlanMateria> materiasDestino() {
        return planDestino.getMaterias().stream()
                .sorted(PlanMateria.porUbicacion())
                .toList();
    }

    public Optional<EstadoMateria> estadoDe(Materia materia) {
        return historial.stream()
                .filter(h -> mismaMateria(h.getMateria(), materia))
                .map(HistorialAcademico::getEstadoGlobal)
                .findFirst();
    }

    public Optional<Integer> horasEnPlanOrigen(Materia materia) {
        return planOrigen.getMaterias().stream()
                .filter(pm -> mismaMateria(pm.getMateria(), materia))
                .map(PlanMateria::getCargaHoraria)
                .findFirst();
    }

    public List<Equivalencia> equivalenciasHacia(Materia materia) {
        return equivalencias.stream()
                .filter(eq -> mismaMateria(eq.getMateriaDestino(), materia))
                .toList();
    }

    public List<Materia> optativasAprobadasEnPlanOrigen() {
        return planOrigen.getMaterias().stream()
                .filter(PlanMateria::isEsOptativa)
                .filter(pm -> estadoDe(pm.getMateria())
                        .filter(estado -> estado == EstadoMateria.APROBADA)
                        .isPresent())
                .map(PlanMateria::getMateria)
                .toList();
    }

    public boolean tieneDestinoConMismaMateria(Materia materia) {
        return planDestino.getMaterias().stream()
                .anyMatch(pm -> mismaMateria(pm.getMateria(), materia));
    }

    public boolean tieneEquivalenciaComoOrigen(Materia materia) {
        return equivalencias.stream()
                .flatMap(eq -> eq.getMateriasOrigen().stream())
                .anyMatch(origen -> mismaMateria(origen, materia));
    }

    private static boolean mismaMateria(Materia a, Materia b) {
        return a != null && b != null && Objects.equals(a.getIdMateria(), b.getIdMateria());
    }
}
