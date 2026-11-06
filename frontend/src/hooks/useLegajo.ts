/* ===== Hook: legajo persistido en localStorage ===== */

import { useCallback, useState } from 'react';

const STORAGE_KEY = 'simulador-legajo';
const FORMATO = /^\d{7,15}$/;

function leerLegajo(): string | null {
  try {
    const guardado = localStorage.getItem(STORAGE_KEY);
    return guardado && FORMATO.test(guardado) ? guardado : null;
  } catch {
    return null;
  }
}

export function useLegajo() {
  const [legajo, setLegajo] = useState<string | null>(leerLegajo);

  const guardar = useCallback((nuevo: string) => {
    const limpio = nuevo.trim();
    try {
      localStorage.setItem(STORAGE_KEY, limpio);
    } catch {
      /* modo incógnito puede bloquear localStorage */
    }
    setLegajo(limpio);
  }, []);

  const limpiar = useCallback(() => {
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      /* ignorado */
    }
    setLegajo(null);
  }, []);

  return { legajo, guardar, limpiar, formatoValido: FORMATO };
}
