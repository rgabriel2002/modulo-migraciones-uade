/* ===== Badge de estado de materia simulada ===== */

import { cn } from '@/utils/cn';

type Estado = 'APROBADA' | 'FINAL' | 'CURSANDO' | 'A_CURSAR';

interface EstadoBadgeProps {
  estado: Estado;
}

const colores: Record<Estado, string> = {
  APROBADA: 'bg-[#9fdc9f] text-[#1f5a2f]',
  FINAL: 'bg-[#ffe066] text-[#8a2f0f]',
  CURSANDO: 'bg-[#eaf2fb] text-[#0f548d]',
  A_CURSAR: 'bg-[#e9edf3] text-[#5d6d85]',
};

export function EstadoBadge({ estado }: EstadoBadgeProps) {
  const etiquetas: Record<Estado, string> = {
    APROBADA: 'Aprobada',
    FINAL: 'Final pendiente',
    CURSANDO: 'Cursando',
    A_CURSAR: 'A cursar',
  };

  return (
    <span className={cn('inline-block rounded px-2 py-0.5 text-[0.75rem] font-medium', colores[estado])}>
      {etiquetas[estado]}
    </span>
  );
}
