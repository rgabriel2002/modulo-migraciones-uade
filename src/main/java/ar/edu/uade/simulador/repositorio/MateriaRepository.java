package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Materia;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MateriaRepository extends JpaRepository<Materia, Integer> {

    Optional<Materia> findByCodigo(String codigo);
}
