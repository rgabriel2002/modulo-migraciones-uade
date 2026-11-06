/* ===== Pantalla de resultado de la simulación ===== */

import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams, useSearchParams } from 'react-router-dom';
import { ApiError } from '@/api/cliente';
import { simulacionesPorModo, type ModoDatos } from '@/api/fixtures';
import { usePlanDetalle } from '@/hooks/usePlanDetalle';
import { useSimulacion } from '@/hooks/useSimulacion';
import { ToggleDatos } from '@/dev/ToggleDatos';
import { normalizarModo } from '@/dev/modoDatos';
import { Banner } from '@/componentes/Banner';
import { Boton } from '@/componentes/Boton';
import { Kpis } from '@/componentes/Kpis';
import { MapaCorrelativas } from '@/componentes/MapaCorrelativas';
import { ModalJson } from '@/componentes/ModalJson';
import { Pestanas } from '@/componentes/Pestanas';
import { TablaResultado } from '@/componentes/TablaResultado';
import {
  IconoCodigo,
  IconoFlecha,
  IconoImpresora,
  IconoInfo,
} from '@/componentes/Iconos';

export function Resultado() {
  const { legajo, planOrigen, planDestino } = useParams<{
    legajo: string;
    planOrigen: string;
    planDestino: string;
  }>();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const { mutate, data, error, isPending } = useSimulacion();
  const [jsonAbierto, setJsonAbierto] = useState(false);
  const ultimaSimulacion = useRef<string | null>(null);

  // Modo de datos del toggle dev-only. En producción siempre es "demo".
  const modo: ModoDatos = import.meta.env.DEV
    ? normalizarModo(searchParams.get('data'))
    : 'demo';

  // Pestaña activa: informe por defecto, mapa con ?vista=mapa.
  const vista = searchParams.get('vista') === 'mapa' ? 'mapa' : 'informe';

  useEffect(() => {
    if (!legajo || !planOrigen || !planDestino) return;
    // Con un fixture no se llama al backend.
    if (modo !== 'demo') return;
    const clave = `${legajo}/${planOrigen}/${planDestino}`;
    if (ultimaSimulacion.current === clave) return;
    ultimaSimulacion.current = clave;
    mutate({
      legajo,
      codigoPlanOrigen: planOrigen,
      codigoPlanDestino: planDestino,
    });
  }, [legajo, planOrigen, planDestino, mutate, modo]);

  const reintentar = () => {
    if (!legajo || !planOrigen || !planDestino) return;
    mutate({
      legajo,
      codigoPlanOrigen: planOrigen,
      codigoPlanDestino: planDestino,
    });
  };

  const errorApi = error instanceof ApiError ? error : null;
  const mensajeError =
    modo === 'demo'
      ? errorApi
        ? errorApi.message
        : error
          ? 'Ocurrió un error inesperado.'
          : null
      : null;

  const respuestaJson =
    modo === 'demo'
      ? data
        ? { status: data.status, body: data.body }
        : errorApi
          ? { status: errorApi.status, body: errorApi.body }
          : null
      : { status: 200, body: simulacionesPorModo[modo] };

  const resultado =
    modo === 'demo' ? (data?.body ?? null) : simulacionesPorModo[modo];

  const planDetalle = usePlanDetalle(
    planDestino ?? null,
    vista === 'mapa' && !!resultado
  );

  const errorPlan =
    planDetalle.error instanceof ApiError ? planDetalle.error : null;
  const mensajePlan = planDetalle.error
    ? (errorPlan?.message ?? 'No se pudo cargar el plan de estudios.')
    : null;

  const cambiarModo = (nuevo: ModoDatos) => {
    const params = new URLSearchParams(searchParams);
    if (nuevo === 'demo') params.delete('data');
    else params.set('data', nuevo);
    setSearchParams(params, { replace: true });
  };

  const cambiarVista = (nueva: string) => {
    const params = new URLSearchParams(searchParams);
    if (nueva === 'informe') params.delete('vista');
    else params.set('vista', nueva);
    setSearchParams(params, { replace: true });
  };

  return (
    <div className="mx-auto max-w-[1280px] px-4 pt-6 pb-7 sm:px-8 sm:pt-9">
      <button
        type="button"
        onClick={() => navigate('/')}
        className="mb-5 inline-flex items-center gap-1.5 text-[0.85rem] font-medium text-blue transition-opacity duration-150 ease-out hover:underline active:opacity-70"
      >
        <IconoFlecha className="h-4 w-4 rotate-180" />
        Volver a la selección
      </button>

      <h1 className="mb-2 font-sans text-[clamp(1.45rem,2.4vw,1.95rem)] font-bold tracking-[-0.01em] text-ink">
        Resultado de la simulación
      </h1>

      {resultado && (
        <p className="mb-7 flex flex-wrap items-center gap-2 text-[0.92rem] text-muted">
          <span className="font-semibold text-ink">
            {resultado.planOrigen.carrera}
          </span>
          <span className="text-[0.78rem]">
            Plan {resultado.planOrigen.codigo}
          </span>
          <IconoFlecha className="h-4 w-4 text-blue" />
          <span className="font-semibold text-ink">
            {resultado.planDestino.carrera}
          </span>
          <span className="text-[0.78rem]">
            Plan {resultado.planDestino.codigo}
          </span>
        </p>
      )}

      {isPending && (
        <div
          className="card flex items-center justify-center gap-3 p-10 text-muted"
          role="status"
          aria-live="polite"
        >
          <span className="spinner" aria-hidden="true" />
          Calculando la simulación…
        </div>
      )}

      {mensajeError && !isPending && (
        <>
          <Banner variant="warn" className="banner-entrada" icono={<IconoInfo />}>
            {mensajeError}
          </Banner>
          <div className="flex flex-wrap gap-3">
            <Boton onClick={reintentar}>Reintentar</Boton>
            <Boton variant="secundario" onClick={() => navigate('/')}>
              Volver
            </Boton>
            {respuestaJson && (
              <Boton
                variant="secundario"
                onClick={() => setJsonAbierto(true)}
              >
                <IconoCodigo className="h-4 w-4" />
                Ver JSON
              </Boton>
            )}
          </div>
        </>
      )}

      {resultado && !isPending && (
        <div className="resultado-entrada space-y-5">
          <Pestanas
            pestanas={[
              { id: 'informe', etiqueta: 'Informe de equivalencias' },
              {
                id: 'mapa',
                etiqueta: (
                  <>
                    Mapa de correlativas
                    <span className="tab-extra"> (Plan de carrera)</span>
                  </>
                ),
              },
            ]}
            activa={vista}
            onChange={cambiarVista}
          />

          <div
            role="tabpanel"
            id="panel-informe"
            aria-labelledby="tab-informe"
            hidden={vista !== 'informe'}
            className="space-y-5"
          >
              <Kpis
                kpis={[
                  {
                    valor: resultado.resumen.materiasReconocidas,
                    etiqueta: 'Materias reconocidas',
                  },
                  {
                    valor: resultado.resumen.aprobadas,
                    etiqueta: 'Aprobadas',
                  },
                  {
                    valor: resultado.resumen.pendientesDeFinal,
                    etiqueta: 'Con final pendiente',
                  },
                  {
                    valor: resultado.resumen.pendientesDeCursado,
                    etiqueta: 'Pendientes de cursado',
                  },
                ]}
              />

              <div className="card">
                <div className="mb-2 flex flex-wrap items-baseline justify-between gap-2">
                  <h2 className="m-0 text-[1rem] font-semibold text-ink">
                    Avance del título intermedio
                  </h2>
                  <span className="text-[0.85rem] text-muted tabular-nums">
                    {resultado.resumen.tituloIntermedio.aprobadas} de{' '}
                    {resultado.resumen.tituloIntermedio.total} materias ·{' '}
                    {resultado.resumen.tituloIntermedio.avance}%
                  </span>
                </div>
                <div className="h-2.5 w-full overflow-hidden rounded-full bg-[#e9edf3]">
                  <div
                    className="h-full rounded-full bg-blue transition-[width] duration-300 ease-out motion-reduce:transition-none"
                    style={{
                      width: `${resultado.resumen.tituloIntermedio.avance}%`,
                    }}
                  />
                </div>
              </div>

              <TablaResultado items={resultado.items} />

              {resultado.optativasSinCorrespondencia.length > 0 && (
                <div className="card">
                  <h2 className="m-0 mb-2 text-[1rem] font-semibold text-ink">
                    Optativas aprobadas sin correspondencia
                  </h2>
                  <ul className="m-0 list-disc pl-5 text-[0.875rem] text-muted">
                    {resultado.optativasSinCorrespondencia.map((m) => (
                      <li key={m.codigo}>
                        {m.codigo} — {m.nombre}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
          </div>

          <div
            role="tabpanel"
            id="panel-mapa"
            aria-labelledby="tab-mapa"
            hidden={vista !== 'mapa'}
            className="space-y-5"
          >
            {vista === 'mapa' && (
              <>
                {planDetalle.isLoading && (
                  <div
                    className="card flex items-center justify-center gap-3 p-10 text-muted"
                    role="status"
                    aria-live="polite"
                  >
                    <span className="spinner" aria-hidden="true" />
                    Cargando el plan de estudios…
                  </div>
                )}

                {mensajePlan && !planDetalle.isLoading && (
                  <>
                    <Banner variant="warn" icono={<IconoInfo />}>
                      {mensajePlan}
                    </Banner>
                    <div>
                      <Boton
                        variant="secundario"
                        onClick={() => planDetalle.refetch()}
                      >
                        Reintentar
                      </Boton>
                    </div>
                  </>
                )}

                {planDetalle.data && !planDetalle.isLoading && (
                  <MapaCorrelativas
                    plan={planDetalle.data.body}
                    items={resultado.items}
                  />
                )}
              </>
            )}
          </div>

          <div className="flex flex-wrap justify-end gap-3">
            <Boton variant="secundario" onClick={() => navigate('/')}>
              Volver
            </Boton>
            <Boton onClick={() => setJsonAbierto(true)}>
              <IconoImpresora className="h-4 w-4" />
              Imprimir Reporte
            </Boton>
          </div>
        </div>
      )}

      {respuestaJson && (
        <ModalJson
          abierto={jsonAbierto}
          onCerrar={() => setJsonAbierto(false)}
          metodo="POST"
          ruta="/api/v1/simulaciones"
          status={respuestaJson.status}
          body={respuestaJson.body}
        />
      )}

      {import.meta.env.DEV && (
        <ToggleDatos modo={modo} onCambiar={cambiarModo} />
      )}
    </div>
  );
}
