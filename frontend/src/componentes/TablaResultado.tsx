/* ===== Tabla de materias simuladas ===== */

import type { ItemSimulado } from '@/api/tipos';
import { EstadoBadge } from './EstadoBadge';

interface TablaResultadoProps {
  items: ItemSimulado[];
}

export function TablaResultado({ items }: TablaResultadoProps) {
  if (items.length === 0) {
    return (
      <div className="card p-6 text-center text-muted">
        No hay materias en el plan destino.
      </div>
    );
  }

  return (
    <>
      <div className="tabla-desktop card overflow-x-auto overscroll-x-contain">
        <table className="w-full min-w-[760px] text-left border-collapse text-[0.875rem]">
        <thead>
          <tr className="bg-[#fbfcfe] border-b border-line">
            <th className="p-3 text-muted font-semibold uppercase tracking-wider">Código</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider">Materia</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider text-right">Año</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider text-right">Cuat.</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider text-right">Hs</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider">Estado</th>
            <th className="p-3 text-muted font-semibold uppercase tracking-wider">Reconocimiento</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.codigo} className="border-b border-line hover:bg-[#fbfcfe]">
              <td className="p-3 text-ink font-medium tabular-nums">{item.codigo}</td>
              <td className="p-3 text-ink text-pretty">{item.nombre}</td>
              <td className="p-3 text-muted text-right tabular-nums">{item.anio ?? '—'}</td>
              <td className="p-3 text-muted text-right tabular-nums">{item.cuatrimestre ?? '—'}</td>
              <td className="p-3 text-muted text-right tabular-nums">{item.cargaHoraria}</td>
              <td className="p-3">
                <EstadoBadge estado={item.estado} />
              </td>
              <td className="p-3 text-muted">
                {item.reconocimiento ? (
                  <>
                    <span className="font-medium text-ink">
                      {item.reconocimiento.tipo === 'EQUIVALENCIA'
                        ? 'Equivalencia por resolución'
                        : 'Misma materia'}
                    </span>
                    {item.reconocimiento.respaldo && (
                      <div className="text-[0.78rem] mt-0.5 text-blue-d">
                        {item.reconocimiento.respaldo}
                      </div>
                    )}
                    {item.reconocimiento.materiasOrigen.length > 0 && (
                      <div className="text-[0.78rem] mt-0.5 text-muted">
                        Origen:{' '}
                        {item.reconocimiento.materiasOrigen
                          .map((m) => m.codigo)
                          .join(', ')}
                      </div>
                    )}
                  </>
                ) : (
                  <span className="italic">Sin reconocimiento</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>

      {/* Mobile: tarjetas apiladas, sin tabla horizontal. */}
      <div className="tabla-mobile">
        <ul className="tabla-m-lista">
          {items.map((item) => (
            <li key={item.codigo} className="tabla-m-item">
              <div className="tabla-m-cab">
                <span className="tabla-m-cod">{item.codigo}</span>
                <EstadoBadge estado={item.estado} />
              </div>
              <div className="tabla-m-nombre">{item.nombre}</div>
              <div className="tabla-m-meta">
                Año {item.anio ?? '—'} · Cuat. {item.cuatrimestre ?? '—'} ·{' '}
                {item.cargaHoraria} hs
              </div>
              {item.reconocimiento ? (
                <div className="tabla-m-rec">
                  <span className="tabla-m-rec-tipo">
                    {item.reconocimiento.tipo === 'EQUIVALENCIA'
                      ? 'Equivalencia por resolución'
                      : 'Misma materia'}
                  </span>
                  {item.reconocimiento.respaldo && (
                    <div className="tabla-m-rec-detalle">
                      {item.reconocimiento.respaldo}
                    </div>
                  )}
                  {item.reconocimiento.materiasOrigen.length > 0 && (
                    <div className="tabla-m-rec-detalle">
                      Origen:{' '}
                      {item.reconocimiento.materiasOrigen
                        .map((m) => m.codigo)
                        .join(', ')}
                    </div>
                  )}
                </div>
              ) : (
                <span className="tabla-m-sin">Sin reconocimiento</span>
              )}
            </li>
          ))}
        </ul>
      </div>
    </>
  );
}
