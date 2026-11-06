/* ===== Pestañas accesibles (patrón WAI-ARIA tabs) ===== */

import { useRef } from 'react';
import { cn } from '@/utils/cn';

export type Pestana = {
  id: string;
  etiqueta: React.ReactNode;
};

interface PestanasProps {
  pestanas: Pestana[];
  activa: string;
  onChange: (id: string) => void;
  className?: string;
}

export function Pestanas({ pestanas, activa, onChange, className }: PestanasProps) {
  const refs = useRef<Array<HTMLButtonElement | null>>([]);

  const mover = (indice: number) => {
    const total = pestanas.length;
    if (total === 0) return;
    const destino = (indice + total) % total;
    onChange(pestanas[destino].id);
    refs.current[destino]?.focus();
  };

  const alTeclear = (
    e: React.KeyboardEvent<HTMLButtonElement>,
    indice: number
  ) => {
    switch (e.key) {
      case 'ArrowRight':
        e.preventDefault();
        mover(indice + 1);
        break;
      case 'ArrowLeft':
        e.preventDefault();
        mover(indice - 1);
        break;
      case 'Home':
        e.preventDefault();
        mover(0);
        break;
      case 'End':
        e.preventDefault();
        mover(pestanas.length - 1);
        break;
    }
  };

  return (
    <div
      role="tablist"
      aria-label="Vistas del resultado"
      className={cn('tabs', className)}
    >
      {pestanas.map((pestana, indice) => {
        const seleccionada = pestana.id === activa;
        return (
          <button
            key={pestana.id}
            ref={(el) => {
              refs.current[indice] = el;
            }}
            type="button"
            role="tab"
            id={`tab-${pestana.id}`}
            aria-selected={seleccionada}
            aria-controls={`panel-${pestana.id}`}
            tabIndex={seleccionada ? 0 : -1}
            className="tab"
            onClick={() => onChange(pestana.id)}
            onKeyDown={(e) => alTeclear(e, indice)}
          >
            {pestana.etiqueta}
          </button>
        );
      })}
    </div>
  );
}
