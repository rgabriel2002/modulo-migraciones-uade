package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.PlanMateria;
import ar.edu.uade.simulador.dominio.PlanMateriaId;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanMateriaRepository extends JpaRepository<PlanMateria, PlanMateriaId> {

    @EntityGraph(attributePaths = "materia")
    List<PlanMateria> findByPlan(PlanEstudio plan);
}
