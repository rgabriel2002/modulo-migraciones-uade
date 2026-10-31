package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Correlatividad;
import ar.edu.uade.simulador.dominio.CorrelatividadId;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrelatividadRepository extends JpaRepository<Correlatividad, CorrelatividadId> {

    @EntityGraph(attributePaths = {"materia", "materiaRequerida"})
    List<Correlatividad> findByPlan(PlanEstudio plan);
}
