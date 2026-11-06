/* ===== Hook: situación académica del alumno ===== */

import { useQuery } from '@tanstack/react-query';
import { getSituacion } from '../api/cliente';

export function useSituacion(legajo: string | null) {
  return useQuery({
    queryKey: ['situacion', legajo],
    queryFn: () => getSituacion(legajo as string),
    enabled: !!legajo,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });
}
