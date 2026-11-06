/* ===== Selector de plan (filtrado por la carrera elegida) ===== */

import type { PlanResumen } from '@/api/tipos';

interface SelectorPlanProps {
  planes: PlanResumen[];
  carrera: string | null;
  value: string | null;
  onChange: (codigoPlan: string) => void;
  disabled?: boolean;
}

export function SelectorPlan({
  planes,
  carrera,
  value,
  onChange,
  disabled = false,
}: SelectorPlanProps) {
  const filtrados = carrera
    ? planes.filter((p) => p.carrera === carrera)
    : [];

  return (
    <div>
      <label className="label" htmlFor="sel-plan">
        Plan
      </label>
      <select
        id="sel-plan"
        className="field"
        value={value ?? ''}
        onChange={(e) => onChange(e.target.value)}
        disabled={disabled || !carrera || filtrados.length === 0}
      >
        <option value="">
          {carrera ? 'Seleccioná un plan' : 'Seleccioná primero una carrera'}
        </option>
        {filtrados.map((p) => (
          <option key={p.codigoPlan} value={p.codigoPlan}>
            Plan {p.codigoPlan}
          </option>
        ))}
      </select>
    </div>
  );
}
