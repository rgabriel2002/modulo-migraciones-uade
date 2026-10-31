package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Estudiante;
import ar.edu.uade.simulador.dominio.HistorialAcademico;
import ar.edu.uade.simulador.dominio.PlanEstudio;
import java.util.List;

public interface FuenteSituacionAcademica {

    Estudiante obtenerEstudiante(String legajo);

    PlanEstudio obtenerPlanActual(String legajo);

    List<HistorialAcademico> obtenerHistorial(String legajo);
}
