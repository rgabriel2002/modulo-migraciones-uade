/* ===== Fixtures de peor caso (solo desarrollo) =====
   Se usan desde el toggle "Demo / Peor caso / Vacío / Uno" de la pantalla
   de Resultado. Los valores respetan los límites del esquema SQL:
   - carrera.nombre: VARCHAR(150)
   - plan_estudio.codigo_plan: VARCHAR(20)
   - materia.codigo: VARCHAR(20)
   - materia.nombre: VARCHAR(150)
   - equivalencia.resolucion: VARCHAR(50)
   - anio_plan: 1..7, cuatrimestre: 1..2 o NULL
*/

import type {
  EstadoSimulado,
  ItemSimulado,
  MateriaRef,
  Reconocimiento,
  Resumen,
  SimulacionResponse,
} from './tipos';

export type ModoDatos = 'demo' | 'peor' | 'vacio' | 'uno';

const ref = (codigo: string, nombre: string): MateriaRef => ({ codigo, nombre });

const crearItem = (
  datos: Pick<ItemSimulado, 'codigo' | 'nombre' | 'estado'> &
    Partial<Omit<ItemSimulado, 'codigo' | 'nombre' | 'estado'>>
): ItemSimulado => ({
  anio: 1,
  cuatrimestre: 1,
  cargaHoraria: 68,
  optativa: false,
  tituloIntermedio: false,
  reconocimiento: null,
  ...datos,
});

function resumenDe(items: ItemSimulado[]): Resumen {
  const contar = (estado: EstadoSimulado) =>
    items.filter((item) => item.estado === estado).length;

  const aprobadas = contar('APROBADA');
  const titulo = items.filter((item) => item.tituloIntermedio);
  const aprobadasTitulo = titulo.filter(
    (item) => item.estado === 'APROBADA'
  ).length;

  return {
    aprobadas,
    pendientesDeFinal: contar('FINAL'),
    pendientesDeCursado: contar('CURSANDO') + contar('A_CURSAR'),
    materiasReconocidas: items.filter((item) => item.reconocimiento !== null)
      .length,
    pendientesPlanActual: 42,
    pendientesPlanNuevo: items.length - aprobadas,
    tituloIntermedio: {
      aprobadas: aprobadasTitulo,
      total: titulo.length,
      avance:
        titulo.length === 0
          ? 0
          : Math.round((100 * aprobadasTitulo) / titulo.length),
    },
  };
}

const planOrigen = {
  codigo: '1121-PLAN-2023-AAAA',
  carrera:
    'Tecnicatura Universitaria en Desarrollo de Software, Sistemas Embebidos y Transformación Digital',
  facultad: 'Facultad de Ingeniería y Ciencias Exactas',
};

const planDestino = {
  codigo: '1621-ING-2026-XXXX',
  carrera:
    'Ingeniería en Informática orientada a Sistemas Distribuidos, Ciberseguridad y Computación en la Nube',
  facultad: 'Facultad de Ingeniería y Ciencias Exactas',
};

const reconocimientoLargo: Reconocimiento = {
  tipo: 'EQUIVALENCIA',
  respaldo: 'Resolución Consejo Superior N° 123/21 - Anexo II',
  materiasOrigen: [
    ref('3.4.225', 'Introducción a la Algoritmia'),
    ref('3.4.226', 'Algoritmos y Estructuras de Datos I'),
    ref('3.4.227', 'Algoritmos y Estructuras de Datos II'),
    ref('3.4.228', 'Paradigmas de Programación'),
    ref('3.4.229', 'Taller de Lenguajes de Programación'),
    ref('3.4.230', 'Sistemas Operativos'),
    ref('3.4.231', 'Redes de Computadoras'),
    ref('3.4.232', 'Base de Datos Avanzada'),
  ],
};

const itemsPeorCaso: ItemSimulado[] = [
  crearItem({
    codigo: '3.4.069',
    nombre:
      'Fundamentos de Arquitectura de Software, Patrones de Diseño y Sistemas Distribuidos de Alta Disponibilidad',
    anio: 1,
    cuatrimestre: 1,
    cargaHoraria: 1024,
    tituloIntermedio: true,
    estado: 'APROBADA',
    reconocimiento: reconocimientoLargo,
  }),
  crearItem({
    codigo: 'SIST-DIST-2026-EXT',
    nombre: 'Sistemas Distribuidos',
    anio: null,
    cuatrimestre: null,
    cargaHoraria: 9999,
    tituloIntermedio: true,
    estado: 'FINAL',
  }),
  crearItem({
    codigo: '9.9.000',
    nombre: '',
    anio: 7,
    cuatrimestre: 2,
    cargaHoraria: 1,
    estado: 'A_CURSAR',
  }),
  crearItem({
    codigo: '3.4.233',
    nombre:
      'Programación Orientada a Objetos y Estructuras de Datos Avanzadas',
    anio: 2,
    cuatrimestre: 1,
    cargaHoraria: 96,
    tituloIntermedio: true,
    estado: 'CURSANDO',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: null,
      materiasOrigen: [ref('3.4.233', 'Programación Orientada a Objetos')],
    },
  }),
  crearItem({
    codigo: '3.4.234',
    nombre: 'Seguridad Informática <script>alert(1)</script>',
    anio: 3,
    cuatrimestre: 1,
    cargaHoraria: 68,
    estado: 'APROBADA',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('3.4.234', 'Seguridad Informática')],
    },
  }),
  crearItem({
    codigo: '3.4.235',
    nombre:
      'Seminario de Investigación Aplicada, Innovación Tecnológica, Emprendedurismo y Transferencia de Conocimiento al Sector Productivo y Social',
    anio: 4,
    cuatrimestre: 2,
    cargaHoraria: 128,
    tituloIntermedio: true,
    estado: 'APROBADA',
    reconocimiento: {
      tipo: 'EQUIVALENCIA',
      respaldo: 'Res. 123/21',
      materiasOrigen: [ref('9.9.005', 'Seminario de Tendencias')],
    },
  }),
  crearItem({
    codigo: '3.4.236',
    nombre:
      'Taller de Innovación y Emprendedurismo — Proyecto Final Integrador',
    anio: 5,
    cuatrimestre: 1,
    cargaHoraria: 160,
    tituloIntermedio: true,
    estado: 'FINAL',
  }),
  crearItem({
    codigo: '3.4.164',
    nombre: 'Sistemas de Información I',
    estado: 'APROBADA',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('3.4.164', 'Sistemas de Información I')],
    },
  }),
  crearItem({
    codigo: '9.9.001',
    nombre: 'Matemática Discreta',
    anio: 1,
    cuatrimestre: 2,
    cargaHoraria: 102,
    estado: 'A_CURSAR',
  }),
  crearItem({
    codigo: '9.9.006',
    nombre: 'Programación I',
    anio: 1,
    cuatrimestre: 2,
    estado: 'CURSANDO',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('9.9.006', 'Programación I')],
    },
  }),
  crearItem({
    codigo: '9.9.002',
    nombre: 'Base de Datos I',
    anio: 2,
    cuatrimestre: 1,
    estado: 'FINAL',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('9.9.002', 'Base de Datos I')],
    },
  }),
  crearItem({
    codigo: '9.9.003',
    nombre: 'Redes de Computadoras',
    anio: 2,
    cuatrimestre: 2,
    estado: 'APROBADA',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('9.9.003', 'Redes de Computadoras')],
    },
  }),
  crearItem({
    codigo: '9.9.004',
    nombre: 'Taller de Innovación',
    anio: 2,
    cuatrimestre: 2,
    optativa: true,
    estado: 'APROBADA',
    reconocimiento: {
      tipo: 'MISMA_MATERIA',
      respaldo: 'Misma materia',
      materiasOrigen: [ref('9.9.004', 'Taller de Innovación')],
    },
  }),
  crearItem({
    codigo: '9.9.007',
    nombre: 'Sistemas Operativos',
    anio: 3,
    cuatrimestre: 1,
    estado: 'CURSANDO',
  }),
  crearItem({
    codigo: '9.9.008',
    nombre: 'Ingeniería de Software',
    anio: 3,
    cuatrimestre: 2,
    estado: 'A_CURSAR',
  }),
  crearItem({
    codigo: '9.9.009',
    nombre: 'Ética Profesional y Legislación Informática',
    anio: 4,
    cuatrimestre: 1,
    estado: 'FINAL',
  }),
];

const relleno: ItemSimulado[] = Array.from({ length: 48 }, (_, indice) => {
  const estados: EstadoSimulado[] = [
    'APROBADA',
    'FINAL',
    'CURSANDO',
    'A_CURSAR',
  ];
  const numero = String(indice + 100).padStart(3, '0');

  return crearItem({
    codigo: `9.9.${numero}`,
    nombre: `Materia de Formación General ${numero}`,
    anio: ((indice % 5) + 1) as number,
    cuatrimestre: indice % 2 === 0 ? 1 : 2,
    cargaHoraria: 32 + (indice % 4) * 32,
    tituloIntermedio: indice < 2,
    estado: estados[indice % estados.length],
  });
});

const itemsPeorCasoCompletos = [...itemsPeorCaso, ...relleno];

const optativasPeorCaso: MateriaRef[] = [
  ref('9.9.005', 'Seminario de Tendencias'),
  ref(
    '9.9.010',
    'Seminario de Tendencias Tecnológicas, Innovación Abierta y Transformación Digital'
  ),
  ref('9.9.011', 'Taller de Comunicación Profesional y Oratoria'),
  ref('9.9.012', 'Introducción a la Inteligencia Artificial'),
  ref('9.9.013', 'Blockchain y Contratos Inteligentes'),
  ref('9.9.014', 'Computación Cuántica Aplicada'),
  ref('9.9.015', 'Gestión de la Diversidad e Inclusión'),
  ref('9.9.016', 'Seminario de Práctica Profesional Supervisada'),
];

export const simulacionPeorCaso: SimulacionResponse = {
  planOrigen,
  planDestino,
  items: itemsPeorCasoCompletos,
  resumen: resumenDe(itemsPeorCasoCompletos),
  optativasSinCorrespondencia: optativasPeorCaso,
};

export const simulacionVacia: SimulacionResponse = {
  planOrigen,
  planDestino,
  items: [],
  resumen: resumenDe([]),
  optativasSinCorrespondencia: [],
};

const itemUnico = crearItem({
  codigo: '3.4.069',
  nombre: 'Fundamentos de Informática',
  tituloIntermedio: true,
  estado: 'APROBADA',
  reconocimiento: {
    tipo: 'EQUIVALENCIA',
    respaldo: 'Res. 123/21',
    materiasOrigen: [ref('3.4.225', 'Introducción a la Algoritmia')],
  },
});

export const simulacionUna: SimulacionResponse = {
  planOrigen,
  planDestino,
  items: [itemUnico],
  resumen: resumenDe([itemUnico]),
  optativasSinCorrespondencia: [],
};

export const simulacionesPorModo: Record<
  Exclude<ModoDatos, 'demo'>,
  SimulacionResponse
> = {
  peor: simulacionPeorCaso,
  vacio: simulacionVacia,
  uno: simulacionUna,
};
