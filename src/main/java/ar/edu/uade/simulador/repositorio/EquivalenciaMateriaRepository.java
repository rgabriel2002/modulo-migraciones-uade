package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.EquivalenciaMateria;
import ar.edu.uade.simulador.dominio.EquivalenciaMateriaId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquivalenciaMateriaRepository extends JpaRepository<EquivalenciaMateria, EquivalenciaMateriaId> {
}
