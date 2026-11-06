/* ===== Selector de carrera de origen (alumnos multi-carrera) =====
   Se muestra solo cuando el legajo tiene más de un plan activo.
*/

import type { PlanRef } from '@/api/tipos';

interface SelectorOrigenProps {
  planes: PlanRef[];
  value: string | null;
  onChange: (codigo: string) => void;
  disabled?: boolean;
}

export function SelectorOrigen({
  planes,
  value,
  onChange,
  disabled = false,
}: SelectorOrigenProps) {
  const opciones = [...planes].sort((a, b) => a.codigo.localeCompare(b.codigo));

  return (
    <div>
      <label className="label" htmlFor="sel-origen">
        Carrera de origen
      </label>
      <select
        id="sel-origen"
        className="field"
        value={value ?? ''}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled || opciones.length === 0}
      >
        <option value="">Seleccioná una carrera</option>
        {opciones.map((p) => (
          <option key={p.codigo} value={p.codigo}>
            {p.carrera} (Plan {p.codigo})
          </option>
        ))}
      </select>
    </div>
  );
}
