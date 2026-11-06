/* ===== Hook: ejecutar la simulación =====
   Devuelve { status, body } para que el visor JSON muestre la respuesta real.
*/

import { useMutation } from '@tanstack/react-query';
import { postSimulacion, type Respuesta } from '../api/cliente';
import type { SimulacionResponse } from '../api/tipos';

type Variables = {
  legajo: string;
  codigoPlanOrigen: string;
  codigoPlanDestino: string;
};

export function useSimulacion() {
  return useMutation<Respuesta<SimulacionResponse>, Error, Variables>({
    mutationFn: ({ legajo, codigoPlanOrigen, codigoPlanDestino }) =>
      postSimulacion(legajo, { codigoPlanOrigen, codigoPlanDestino }),
  });
}
