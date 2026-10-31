package ar.edu.uade.simulador.repositorio;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquivalenciaRepository extends JpaRepository<Equivalencia, Integer> {

    @Query("""
            select distinct e from Equivalencia e
            join fetch e.materias em
            join fetch em.materia
            where e.planOrigen = :origen
              and e.planDestino = :destino
              and e.vigenteDesde <= :fecha
              and (e.vigenteHasta is null or e.vigenteHasta >= :fecha)
            """)
    List<Equivalencia> findVigentes(
            @Param("origen") PlanEstudio origen,
            @Param("destino") PlanEstudio destino,
            @Param("fecha") LocalDate fecha);

    @Query("""
            select distinct e from Equivalencia e
            join fetch e.materias em
            join fetch em.materia
            join fetch e.planOrigen
            join fetch e.planDestino
            order by e.vigenteDesde desc, e.idEquivalencia
            """)
    List<Equivalencia> buscarTodas();
}
