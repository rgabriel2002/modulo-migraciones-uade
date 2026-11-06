/* ===== Selector de carrera (agrupa los planes destino por carrera) ===== */

import type { PlanResumen } from '@/api/tipos';

interface SelectorCarreraProps {
  planes: PlanResumen[];
  facultad: string | null;
  carrera: string | null;
  onChange: (carrera: string) => void;
  disabled?: boolean;
}

export function SelectorCarrera({
  planes,
  facultad,
  carrera,
  onChange,
  disabled = false,
}: SelectorCarreraProps) {
  const carreras = facultad
    ? Array.from(
        new Set(
          planes.filter((p) => p.facultad === facultad).map((p) => p.carrera)
        )
      ).sort()
    : [];

  return (
    <div>
      <label className="label" htmlFor="sel-carrera">
        Carrera
      </label>
      <select
        id="sel-carrera"
        className="field"
        value={carrera ?? ''}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled || carreras.length === 0}
      >
        <option value="">
          {facultad ? 'Seleccioná una carrera' : 'Seleccioná primero una facultad'}
        </option>
        {carreras.map((c) => (
          <option key={c} value={c}>
            {c}
          </option>
        ))}
      </select>
    </div>
  );
}
