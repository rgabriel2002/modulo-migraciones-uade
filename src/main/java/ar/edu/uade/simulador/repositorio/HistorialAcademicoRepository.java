package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistorialAcademicoRepository extends JpaRepository<HistorialAcademico, UUID> {

    @EntityGraph(attributePaths = "materia")
    List<HistorialAcademico> findByEstudiante(Estudiante estudiante);

    @Query("""
            select distinct h from HistorialAcademico h
            join fetch h.materia
            left join fetch h.cursadas
            where h.estudiante = :estudiante
            """)
    List<HistorialAcademico> buscarConCursadas(@Param("estudiante") Estudiante estudiante);
}
