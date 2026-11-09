/* ===== Mapa de correlativas (plan de carrera) =====
   Dibuja las materias del plan destino agrupadas por año/cuatrimestre y
   traza las correlativas con SVG propio. Solo lectura: los estados vienen
   de la simulación.
*/

import {
  Fragment,
  useLayoutEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import type {
  EstadoSimulado,
  ItemSimulado,
  MateriaPlan,
  PlanDetalle,
} from '@/api/tipos';
import { layoutMapa } from '@/utils/mapa';
import { cn } from '@/utils/cn';

interface MapaCorrelativasProps {
  plan: PlanDetalle;
  items: ItemSimulado[];
}

type Flecha = {
  a: string;
  b: string;
  d: string;
};

const ORDINAL = [
  '',
  '1er Año',
  '2do Año',
  '3er Año',
  '4to Año',
  '5to Año',
  '6to Año',
  '7mo Año',
];

const ETIQUETA_ESTADO: Record<EstadoSimulado, string> = {
  APROBADA: 'Aprobada',
  FINAL: 'Final pendiente',
  CURSANDO: 'Cursando',
  A_CURSAR: 'A cursar',
};

const CLASE_ESTADO: Record<EstadoSimulado, string> = {
  APROBADA: 'es-aprobada',
  FINAL: 'es-final',
  CURSANDO: 'es-cursando',
  A_CURSAR: 'es-a-cursar',
};

function etiquetaMateria(materia: MateriaPlan, estado: EstadoSimulado) {
  const correlativas =
    materia.correlativas.length > 0
      ? `. Correlativas: ${materia.correlativas
          .map((c) => `${c.nombre} (${c.tipo === 'FINAL' ? 'final' : 'cursada'})`)
          .join(', ')}`
      : '';
  return `${materia.nombre}, código ${materia.codigo}. ${ETIQUETA_ESTADO[estado]}${correlativas}`;
}

export function MapaCorrelativas({ plan, items }: MapaCorrelativasProps) {
  const [foco, setFoco] = useState<string | null>(null);
  const [flechas, setFlechas] = useState<Flecha[]>([]);
  const gridRef = useRef<HTMLDivElement | null>(null);
  const vivoRef = useRef(true);

  const layout = useMemo(() => layoutMapa(plan.materias), [plan.materias]);

  const estadoPorCodigo = useMemo(() => {
    const mapa = new Map<string, EstadoSimulado>();
    for (const item of items) mapa.set(item.codigo, item.estado);
    return mapa;
  }, [items]);

  const porCodigo = useMemo(() => {
    const mapa = new Map<string, MateriaPlan>();
    for (const materia of plan.materias) mapa.set(materia.codigo, materia);
    return mapa;
  }, [plan.materias]);

  const relacionadas = useMemo(() => {
    if (!foco) return null;
    const set = new Set<string>([foco]);
    const materia = porCodigo.get(foco);
    materia?.correlativas.forEach((c) => set.add(c.codigo));
    for (const otra of plan.materias) {
      if (otra.correlativas.some((c) => c.codigo === foco)) {
        set.add(otra.codigo);
      }
    }
    return set;
  }, [foco, porCodigo, plan.materias]);

  // Las flechas dependen del layout real (anchos, saltos de línea, fuentes),
  // así que se recalculan después de montar y cuando cambia el tamaño.
  useLayoutEffect(() => {
    const grid = gridRef.current;
    if (!grid) return;

    vivoRef.current = true;

    const medir = () => {
      if (!vivoRef.current) return;
      const g = grid.getBoundingClientRect();

      const caja = (codigo: string) => {
        const el = grid.querySelector<HTMLElement>(
          `[data-codigo="${codigo}"]`
        );
        if (!el) return null;
        const b = el.getBoundingClientRect();
        return {
          row: Number(el.dataset.fila ?? 0),
          l: b.left - g.left,
          t: b.top - g.top,
          b: b.bottom - g.top,
          cx: (b.left + b.right) / 2 - g.left,
        };
      };

      const nuevas: Flecha[] = [];
      let k = 0;

      for (const materia of plan.materias) {
        for (const correlativa of materia.correlativas) {
          const a = caja(correlativa.codigo);
          const b = caja(materia.codigo);
          if (!a || !b) continue;

          const off = ((k++ % 3) - 1) * 5;
          let d: string;

          if (b.row - a.row === 1) {
            if (Math.abs(a.cx - b.cx) < 2) {
              d = `M${a.cx},${a.b} V${b.t}`;
            } else {
              const ym = (a.b + b.t) / 2 + off;
              d = `M${a.cx},${a.b} V${ym} H${b.cx + off} V${b.t}`;
            }
          } else if (b.row === a.row) {
            // Correlativa en el mismo cuatrimestre: se rodea por abajo.
            const y = a.b + 12 + off;
            d = `M${a.cx},${a.b} V${y} H${b.cx} V${b.b}`;
          } else {
            const y1 = a.b + 14 + off;
            const y2 = b.t - 14 + off;
            const gx = b.l - 13 + off;
            d = `M${a.cx},${a.b} V${y1} H${gx} V${y2} H${b.cx + off} V${b.t}`;
          }

          nuevas.push({ a: correlativa.codigo, b: materia.codigo, d });
        }
      }

      setFlechas(nuevas);
    };

    medir();

    const observador = new ResizeObserver(medir);
    observador.observe(grid);
    window.addEventListener('resize', medir);
    document.fonts?.ready.then(medir).catch(() => undefined);

    return () => {
      vivoRef.current = false;
      observador.disconnect();
      window.removeEventListener('resize', medir);
    };
  }, [layout, plan.materias]);

  const totalHoras = plan.materias.reduce(
    (total, materia) => total + materia.cargaHoraria,
    0
  );
  const aprobadas = plan.materias.filter(
    (m) => estadoPorCodigo.get(m.codigo) === 'APROBADA'
  ).length;
  const finales = plan.materias.filter(
    (m) => estadoPorCodigo.get(m.codigo) === 'FINAL'
  ).length;
  const cursando = plan.materias.filter(
    (m) => estadoPorCodigo.get(m.codigo) === 'CURSANDO'
  ).length;

  return (
    <section className="mapa" aria-label="Mapa de correlativas del plan">
      <h2 className="mapa-titulo">
        {plan.carrera} · Plan {plan.codigoPlan}
      </h2>

      <div className="mapa-leyenda" aria-label="Referencias de estado">
        <span>
          <i className="muestra es-aprobada" aria-hidden="true" />
          Aprobada / otorgada
        </span>
        <span>
          <i className="muestra es-final" aria-hidden="true" />
          Final pendiente
        </span>
        <span>
          <i className="muestra es-cursando" aria-hidden="true" />
          Cursando
        </span>
        <span>
          <i className="muestra es-a-cursar" aria-hidden="true" />
          A cursar
        </span>
        <span>
          <i className="muestra es-optativa" aria-hidden="true" />
          Optativa
        </span>
      </div>

      <p className="mapa-hint">
        Pasá el mouse (o tocá) una materia para resaltar sus correlativas.
      </p>
      <p className="mapa-scroll-hint">
        Deslizá para ver el plan completo.
      </p>

      {plan.materias.length === 0 ? (
        <p className="mapa-sin-materias">
          El plan no tiene materias cargadas.
        </p>
      ) : (
        <>
          <div className="mapa-desktop">
            <div className="mapa-scroll">
              <div
                ref={gridRef}
                className={cn('mapa-grid', foco && 'is-foco')}
                onPointerLeave={(e) => {
                  if (e.pointerType === 'mouse') setFoco(null);
                }}
                onClick={(e) => {
                  const objetivo = e.target as HTMLElement;
                  if (!objetivo.closest('[data-codigo]')) setFoco(null);
                }}
              >
                <svg className="mapa-flechas" aria-hidden="true">
                  <defs>
                    <marker
                      id="mapa-punta"
                      viewBox="0 0 10 10"
                      refX="9"
                      refY="5"
                      markerWidth="7"
                      markerHeight="7"
                      orient="auto"
                    >
                      <path d="M0,0 L10,5 L0,10 z" fill="#3b64b5" />
                    </marker>
                  </defs>
                  {flechas.map((flecha) => {
                    const resaltada =
                      foco !== null &&
                      (flecha.a === foco || flecha.b === foco);
                    return (
                      <path
                        key={`${flecha.a}-${flecha.b}`}
                        d={flecha.d}
                        fill="none"
                        stroke="#3b64b5"
                        strokeWidth={1.6}
                        markerEnd="url(#mapa-punta)"
                        className={resaltada ? 'is-on' : undefined}
                      />
                    );
                  })}
                </svg>

                {layout.filas.map((fila, indice) => {
                  const mostrarAnio =
                    indice === 0 ||
                    layout.filas[indice - 1].anio !== fila.anio;
                  const celdas: Array<MateriaPlan | null> = Array.from(
                    { length: layout.ncols },
                    () => null
                  );
                  for (const materia of fila.materias) {
                    celdas[layout.columna[materia.codigo]] = materia;
                  }

                  return (
                    <Fragment
                      key={`${fila.anio}-${fila.cuatrimestre ?? 'anual'}-${indice}`}
                    >
                      {mostrarAnio && (
                        <div className="mapa-anio">
                          {ORDINAL[fila.anio] ?? `${fila.anio}° Año`}
                        </div>
                      )}
                      <div
                        className="mapa-fila"
                        style={{
                          gridTemplateColumns: `repeat(${layout.ncols}, minmax(0, 1fr))`,
                        }}
                      >
                        {celdas.map((materia, celda) => {
                          if (!materia) {
                            return (
                              <div
                                key={`vacio-${celda}`}
                                className="mapa-mat mapa-vacio"
                                aria-hidden="true"
                              />
                            );
                          }

                          const estado =
                            estadoPorCodigo.get(materia.codigo) ?? 'A_CURSAR';
                          const resaltada =
                            relacionadas?.has(materia.codigo) ?? false;

                          return (
                            <div
                              key={materia.codigo}
                              data-codigo={materia.codigo}
                              data-fila={fila.indice}
                              tabIndex={0}
                              role="group"
                              aria-label={etiquetaMateria(materia, estado)}
                              className={cn(
                                'mapa-mat',
                                CLASE_ESTADO[estado],
                                materia.optativa && 'es-optativa',
                                foco && resaltada && 'is-on'
                              )}
                              onPointerEnter={(e) => {
                                if (e.pointerType === 'mouse') {
                                  setFoco(materia.codigo);
                                }
                              }}
                              onFocus={(e) => {
                                if (e.currentTarget.matches(':focus-visible')) {
                                  setFoco(materia.codigo);
                                }
                              }}
                              onBlur={() => setFoco(null)}
                              onClick={() => {
                                // En pantallas táctiles el tap alterna el
                                // resaltado; en escritorio lo maneja el hover,
                                // por eso el tap no actúa en ese caso.
                                const soloHover = window.matchMedia(
                                  '(hover: hover) and (pointer: fine)'
                                ).matches;
                                if (soloHover) return;
                                setFoco((prev) =>
                                  prev === materia.codigo
                                    ? null
                                    : materia.codigo
                                );
                              }}
                              onKeyDown={(e) => {
                                if (e.key === 'Escape') setFoco(null);
                              }}
                            >
                              <span className="mapa-cod">
                                {materia.codigo}
                              </span>
                              <span className="mapa-nombre">
                                {materia.nombre}
                              </span>
                              {materia.optativa && (
                                <span className="mapa-tag">Optativa</span>
                              )}
                            </div>
                          );
                        })}
                      </div>
                    </Fragment>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Vista mobile: lista vertical, sin flechas ni scroll horizontal. */}
          <div className="mapa-mobile">
            {layout.filas.map((fila, indice) => {
              const mostrarAnio =
                indice === 0 ||
                layout.filas[indice - 1].anio !== fila.anio;
              return (
                <Fragment
                  key={`m-${fila.anio}-${fila.cuatrimestre ?? 'anual'}-${indice}`}
                >
                  {mostrarAnio && (
                    <div className="mapa-m-anio">
                      {ORDINAL[fila.anio] ?? `${fila.anio}° Año`}
                    </div>
                  )}
                  <div className="mapa-m-cuat">
                    Cuatrimestre {fila.cuatrimestre ?? 'anual'}
                  </div>
                  <ul className="mapa-m-lista">
                    {fila.materias.map((materia) => {
                      const estado =
                        estadoPorCodigo.get(materia.codigo) ?? 'A_CURSAR';
                      return (
                        <li
                          key={materia.codigo}
                          className={cn(
                            'mapa-m-mat',
                            CLASE_ESTADO[estado],
                            materia.optativa && 'es-optativa'
                          )}
                        >
                          <div className="mapa-m-cab">
                            <span className="mapa-cod">{materia.codigo}</span>
                            <span className="mapa-m-estado">
                              {ETIQUETA_ESTADO[estado]}
                            </span>
                          </div>
                          <span className="mapa-m-nombre">
                            {materia.nombre}
                          </span>
                          {materia.optativa && (
                            <span className="mapa-tag">Optativa</span>
                          )}
                          {materia.correlativas.length > 0 && (
                            <ul className="mapa-m-corr">
                              {materia.correlativas.map((c) => (
                                <li key={c.codigo} className="mapa-m-chip">
                                  Requiere: {c.nombre}
                                  {c.tipo === 'FINAL' ? ' (final)' : ''}
                                </li>
                              ))}
                            </ul>
                          )}
                        </li>
                      );
                    })}
                  </ul>
                </Fragment>
              );
            })}
          </div>
        </>
      )}

      <div className="mapa-pie">
        <span>Carga horaria del plan: {totalHoras} hs</span>
        <span>
          Aprobadas: {aprobadas} de {plan.materias.length}
          {finales > 0 && ` · Final pendiente: ${finales}`}
          {cursando > 0 && ` · Cursando: ${cursando}`}
        </span>
      </div>
    </section>
  );
}
