/* ===== Hook: planes destino disponibles =====
   Refetchea cuando cambia el origen elegido: el backend excluye ese plan
   del listado de destinos.
*/

import { useQuery } from '@tanstack/react-query';
import { getPlanesDestino } from '../api/cliente';

export function usePlanes(legajo: string | null, origenExcluir: string | null) {
  return useQuery({
    queryKey: ['planes', legajo, origenExcluir],
    queryFn: () => getPlanesDestino(legajo as string, origenExcluir ?? undefined),
    enabled: !!legajo,
    staleTime: 5 * 60 * 1000,
    retry: false,
  });
}
