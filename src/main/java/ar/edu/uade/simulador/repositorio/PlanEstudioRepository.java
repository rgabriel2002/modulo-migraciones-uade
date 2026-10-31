package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.PlanEstudio;
import ar.edu.uade.simulador.dominio.enums.EstadoPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlanEstudioRepository extends JpaRepository<PlanEstudio, Integer> {

    Optional<PlanEstudio> findByCodigoPlan(String codigo);

    @EntityGraph(attributePaths = {"carrera", "materias", "materias.materia"})
    @Query("select p from PlanEstudio p where p.codigoPlan = :codigo")
    Optional<PlanEstudio> buscarDetalle(@Param("codigo") String codigo);

    @EntityGraph(attributePaths = "carrera")
    List<PlanEstudio> findByEstado(EstadoPlan estado);
}
