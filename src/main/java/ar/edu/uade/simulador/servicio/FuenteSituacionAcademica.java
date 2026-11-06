package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.EstudiantePlan;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import java.util.List;

public interface FuenteSituacionAcademica {

    Estudiante obtenerEstudiante(String legajo);

    List<EstudiantePlan> obtenerPlanesActivos(String legajo);

    PlanEstudio obtenerPlanActivo(String legajo, String codigoPlan);

    List<HistorialAcademico> obtenerHistorial(String legajo);
}
