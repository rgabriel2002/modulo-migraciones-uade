/* ===== Layout del mapa de correlativas =====
   Puro y sin React: agrupa las materias por año/cuatrimestre y les asigna
   una columna para que cada materia quede alineada bajo su correlativa.
   Misma heurística que el prototipo HTML.
*/

import type { MateriaPlan } from '@/api/tipos';

export type FilaMapa = {
  anio: number;
  cuatrimestre: number | null;
  materias: MateriaPlan[];
  indice: number;
};

export type LayoutMapa = {
  filas: FilaMapa[];
  columna: Record<string, number>;
  filaDe: Record<string, number>;
  ncols: number;
};

const ordenCuatrimestre = (q: number | null) => (q === null ? 3 : q);

export function layoutMapa(materias: MateriaPlan[]): LayoutMapa {
  const filas: FilaMapa[] = [];

  for (const materia of materias) {
    let fila = filas.find(
      (f) => f.anio === materia.anio && f.cuatrimestre === materia.cuatrimestre
    );
    if (!fila) {
      fila = {
        anio: materia.anio,
        cuatrimestre: materia.cuatrimestre,
        materias: [],
        indice: 0,
      };
      filas.push(fila);
    }
    fila.materias.push(materia);
  }

  filas.sort(
    (a, b) =>
      a.anio - b.anio ||
      ordenCuatrimestre(a.cuatrimestre) - ordenCuatrimestre(b.cuatrimestre)
  );
  filas.forEach((fila, indice) => {
    fila.indice = indice;
  });

  const ncolsInicial = Math.max(1, ...filas.map((f) => f.materias.length));
  const columna: Record<string, number> = {};
  const filaDe: Record<string, number> = {};

  for (const fila of filas) {
    for (const materia of fila.materias) {
      filaDe[materia.codigo] = fila.indice;
    }

    const usadas = new Set<number>();
    const orden = [...fila.materias].sort((a, b) => {
      const ka = a.correlativas.length > 0 ? a.correlativas.length : 99;
      const kb = b.correlativas.length > 0 ? b.correlativas.length : 99;
      return ka - kb;
    });

    for (const materia of orden) {
      const preferidas = materia.correlativas
        .map((c) => columna[c.codigo])
        .filter((c): c is number => c !== undefined);

      let destino = preferidas.find((c) => !usadas.has(c));
      if (destino === undefined) {
        const base = preferidas.length > 0 ? preferidas[0] : 0;
        const libres = Array.from({ length: ncolsInicial }, (_, i) => i)
          .filter((i) => !usadas.has(i))
          .sort((x, y) => Math.abs(x - base) - Math.abs(y - base) || x - y);
        destino = libres[0] ?? usadas.size;
      }

      columna[materia.codigo] = destino;
      usadas.add(destino);
    }
  }

  const ncols = Math.max(
    1,
    ncolsInicial,
    ...Object.values(columna).map((c) => c + 1)
  );

  return { filas, columna, filaDe, ncols };
}
