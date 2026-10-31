package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Estudiante;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {

    Optional<Estudiante> findByLegajo(String legajo);
}
