package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialAcademicoRepository extends JpaRepository<HistorialAcademico, UUID> {

    @EntityGraph(attributePaths = "materia")
    List<HistorialAcademico> findByEstudiante(Estudiante estudiante);
}
