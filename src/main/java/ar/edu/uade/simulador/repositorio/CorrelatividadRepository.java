package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Correlatividad;
import ar.edu.uade.simulador.dominio.CorrelatividadId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorrelatividadRepository extends JpaRepository<Correlatividad, CorrelatividadId> {
}
