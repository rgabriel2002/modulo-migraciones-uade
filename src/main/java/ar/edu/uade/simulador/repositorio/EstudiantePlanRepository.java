package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.EstudiantePlan;
import ar.edu.uade.simulador.dominio.EstudiantePlanId;
import ar.edu.uade.simulador.dominio.enums.EstadoInscripcion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstudiantePlanRepository extends JpaRepository<EstudiantePlan, EstudiantePlanId> {

    /**
     * Todas las inscripciones activas del estudiante: un legajo puede estar
     * anotado en más de una carrera a la vez.
     */
    @Query("""
            select ep from EstudiantePlan ep
            join fetch ep.plan p
            join fetch p.carrera c
            join fetch c.facultad
            where ep.estudiante = :estudiante and ep.estado = :estado
            """)
    List<EstudiantePlan> buscarPorEstudianteYEstado(
            @Param("estudiante") Estudiante estudiante,
            @Param("estado") EstadoInscripcion estado);

    /** Inscripción activa del estudiante en un plan puntual. */
    @Query("""
            select ep from EstudiantePlan ep
            join fetch ep.plan p
            join fetch p.carrera c
            join fetch c.facultad
            left join fetch p.materias pm
            left join fetch pm.materia
            where ep.estudiante = :estudiante
              and ep.estado = :estado
              and p.codigoPlan = :codigoPlan
            """)
    Optional<EstudiantePlan> buscarPorEstudianteEstadoYPlan(
            @Param("estudiante") Estudiante estudiante,
            @Param("estado") EstadoInscripcion estado,
            @Param("codigoPlan") String codigoPlan);
}
