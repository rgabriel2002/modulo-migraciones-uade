/* ===== Hook: detalle del plan destino (materias y correlativas) ===== */

import { useQuery } from '@tanstack/react-query';
import { getPlanDetalle } from '../api/cliente';

export function usePlanDetalle(codigo: string | null, habilitado: boolean) {
  return useQuery({
    queryKey: ['plan', codigo],
    queryFn: () => getPlanDetalle(codigo as string),
    enabled: habilitado && !!codigo,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });
}
