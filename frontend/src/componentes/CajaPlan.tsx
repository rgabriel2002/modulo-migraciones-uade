/* ===== Caja Plan: Origen / Destino ===== */

import { cn } from '@/utils/cn';

interface CajaPlanProps {
  titulo: string;
  subtitulo: string;
  children: React.ReactNode;
  icono?: React.ReactNode;
  className?: string;
}

export function CajaPlan({
  titulo,
  subtitulo,
  children,
  icono,
  className,
}: CajaPlanProps) {
  return (
    <div className={cn('card', className)}>
      <div className="flex items-center gap-2 mb-3">
        {icono && <span className="text-blue">{icono}</span>}
        <div>
          <div className="font-semibold text-ink">{titulo}</div>
          <div className="text-[0.875rem] text-muted">{subtitulo}</div>
        </div>
      </div>
      <div className="space-y-3">{children}</div>
    </div>
  );
}
