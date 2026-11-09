/* ===== Cliente HTTP =====
   Envía X-Legajo en cada request, devuelve { status, body } y traduce
   los errores del backend (400, 404, 409) a ApiError con el cuerpo crudo.
*/

import type {
  ErrorResponse,
  PlanDetalle,
  PlanResumen,
  SimulacionRequest,
  SimulacionResponse,
  SituacionAcademica,
} from './tipos';

export type Respuesta<T> = {
  status: number;
  body: T;
};

export class ApiError extends Error {
  readonly status: number;
  readonly body: unknown;

  constructor(status: number, mensaje: string, body: unknown) {
    super(mensaje);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

function headers(legajo?: string): HeadersInit {
  const h: Record<string, string> = {};
  if (legajo) h['X-Legajo'] = legajo;
  return h;
}

async function leerCuerpo(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? '';
  if (contentType.includes('application/json')) {
    return response.json();
  }
  return response.text();
}

function mensajeDe(body: unknown, status: number): string {
  if (body && typeof body === 'object' && 'mensaje' in body) {
    const mensaje = (body as ErrorResponse).mensaje;
    if (typeof mensaje === 'string' && mensaje.length > 0) return mensaje;
  }
  return `Error ${status}`;
}

async function pedir<T>(path: string, init?: RequestInit): Promise<Respuesta<T>> {
  let response: Response;

  try {
    response = await fetch(path, init);
  } catch {
    throw new ApiError(
      0,
      'No se pudo conectar con el servidor. Verificá que el backend esté corriendo.',
      null
    );
  }

  const body = await leerCuerpo(response);

  if (!response.ok) {
    throw new ApiError(response.status, mensajeDe(body, response.status), body);
  }

  return { status: response.status, body: body as T };
}

export function getSituacion(legajo: string): Promise<Respuesta<SituacionAcademica>> {
  return pedir<SituacionAcademica>('/api/v1/estudiantes/situacion', {
    headers: headers(legajo),
  });
}

export function getPlanesDestino(
  legajo: string,
  excluir?: string
): Promise<Respuesta<PlanResumen[]>> {
  const query = excluir
    ? `?excluir=${encodeURIComponent(excluir)}`
    : '';
  return pedir<PlanResumen[]>(`/api/v1/planes${query}`, {
    headers: headers(legajo),
  });
}

export function getPlanDetalle(codigo: string): Promise<Respuesta<PlanDetalle>> {
  return pedir<PlanDetalle>(`/api/v1/planes/${encodeURIComponent(codigo)}`);
}

export function postSimulacion(
  legajo: string,
  request: SimulacionRequest
): Promise<Respuesta<SimulacionResponse>> {
  return pedir<SimulacionResponse>('/api/v1/simulaciones', {
    method: 'POST',
    headers: {
      ...headers(legajo),
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });
}

/* Descarga el PDF del reporte. El backend recalcula la simulación en el
   servidor: el documento sale de datos reales, no de lo que envíe el cliente. */
export async function descargarReportePdf(
  legajo: string,
  codigoPlanOrigen: string,
  codigoPlanDestino: string
): Promise<Blob> {
  let response: Response;
  try {
    response = await fetch('/api/v1/reportes/simulacion', {
      method: 'POST',
      headers: {
        ...headers(legajo),
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ codigoPlanOrigen, codigoPlanDestino }),
    });
  } catch {
    throw new ApiError(
      0,
      'No se pudo conectar con el servidor. Verificá que el backend esté corriendo.',
      null
    );
  }

  if (!response.ok) {
    const body = await leerCuerpo(response);
    throw new ApiError(response.status, mensajeDe(body, response.status), body);
  }
  return response.blob();
}
