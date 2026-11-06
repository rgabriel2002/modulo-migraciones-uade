/* ===== Espejo de los records Java del backend ===== */

export type MateriaRef = {
  codigo: string;
  nombre: string;
};

export type PlanRef = {
  codigo: string;
  carrera: string;
  facultad: string;
};

export type Reconocimiento = {
  tipo: 'MISMA_MATERIA' | 'EQUIVALENCIA';
  respaldo: string | null;
  materiasOrigen: MateriaRef[];
};

export type EstadoSimulado =
  | 'APROBADA'
  | 'FINAL'
  | 'CURSANDO'
  | 'A_CURSAR';

export type ItemSimulado = {
  codigo: string;
  nombre: string;
  anio: number | null;
  cuatrimestre: number | null;
  cargaHoraria: number;
  optativa: boolean;
  tituloIntermedio: boolean;
  estado: EstadoSimulado;
  reconocimiento: Reconocimiento | null;
};

export type TituloIntermedio = {
  aprobadas: number;
  total: number;
  avance: number;
};

export type Resumen = {
  aprobadas: number;
  pendientesDeFinal: number;
  pendientesDeCursado: number;
  materiasReconocidas: number;
  pendientesPlanActual: number;
  pendientesPlanNuevo: number;
  tituloIntermedio: TituloIntermedio;
};

export type SimulacionResponse = {
  planOrigen: PlanRef;
  planDestino: PlanRef;
  items: ItemSimulado[];
  resumen: Resumen;
  optativasSinCorrespondencia: MateriaRef[];
};

export type SituacionAcademica = {
  legajo: string;
  planesActivos: PlanRef[];
};

export type PlanResumen = {
  codigoPlan: string;
  carrera: string;
  facultad: string;
  estado: string;
};

export type Correlatividad = {
  codigo: string;
  nombre: string;
  tipo: 'CURSADA' | 'FINAL';
};

export type MateriaPlan = {
  codigo: string;
  nombre: string;
  anio: number;
  cuatrimestre: number | null;
  cargaHoraria: number;
  optativa: boolean;
  tituloIntermedio: boolean;
  correlativas: Correlatividad[];
};

export type PlanDetalle = {
  codigoPlan: string;
  carrera: string;
  materias: MateriaPlan[];
};

export type ErrorResponse = {
  timestamp: string;
  status: number;
  error: string;
  mensaje: string;
  path: string;
};

export type SimulacionRequest = {
  codigoPlanOrigen: string;
  codigoPlanDestino: string;
};
