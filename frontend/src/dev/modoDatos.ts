/* ===== Modos del toggle de datos (solo desarrollo) ===== */

import type { ModoDatos } from '@/api/fixtures';

export const MODOS: Array<{ id: ModoDatos; etiqueta: string }> = [
  { id: 'demo', etiqueta: 'Demo' },
  { id: 'peor', etiqueta: 'Peor caso' },
  { id: 'vacio', etiqueta: 'Vacío' },
  { id: 'uno', etiqueta: 'Uno' },
];

export function normalizarModo(valor: string | null): ModoDatos {
  return valor === 'peor' || valor === 'vacio' || valor === 'uno'
    ? valor
    : 'demo';
}
