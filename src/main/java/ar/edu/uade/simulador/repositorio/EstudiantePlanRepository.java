package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.EstudiantePlan;
import ar.edu.uade.simulador.dominio.EstudiantePlanId;
import ar.edu.uade.simulador.dominio.enums.EstadoInscripcion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EstudiantePlanRepository extends JpaRepository<EstudiantePlan, EstudiantePlanId> {

    @Query("""
            select ep from EstudiantePlan ep
            join fetch ep.plan p
            join fetch p.carrera
            left join fetch p.materias pm
            left join fetch pm.materia
            where ep.estudiante = :estudiante and ep.estado = :estado
            """)
    Optional<EstudiantePlan> buscarPorEstudianteYEstado(
            @Param("estudiante") Estudiante estudiante,
            @Param("estado") EstadoInscripcion estado);
}
