package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarreraRepository extends JpaRepository<Carrera, Integer> {
}
