/* ===== Selector de facultad (primer nivel del destino) ===== */

import type { PlanResumen } from '@/api/tipos';

interface SelectorFacultadProps {
  planes: PlanResumen[];
  facultad: string | null;
  onChange: (facultad: string) => void;
  disabled?: boolean;
}

export function SelectorFacultad({
  planes,
  facultad,
  onChange,
  disabled = false,
}: SelectorFacultadProps) {
  const facultades = Array.from(new Set(planes.map((p) => p.facultad))).sort();

  return (
    <div>
      <label className="label" htmlFor="sel-facultad">
        Facultad
      </label>
      <select
        id="sel-facultad"
        className="field"
        value={facultad ?? ''}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled || facultades.length === 0}
      >
        <option value="">Seleccioná una facultad</option>
        {facultades.map((f) => (
          <option key={f} value={f}>
            {f}
          </option>
        ))}
      </select>
    </div>
  );
}
