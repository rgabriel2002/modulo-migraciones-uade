/* ===== Visor del JSON crudo del endpoint ===== */

import { useEffect } from 'react';
import { cn } from '@/utils/cn';

interface ModalJsonProps {
  abierto: boolean;
  onCerrar: () => void;
  metodo: string;
  ruta: string;
  status: number;
  body: unknown;
}

export function ModalJson({
  abierto,
  onCerrar,
  metodo,
  ruta,
  status,
  body,
}: ModalJsonProps) {
  useEffect(() => {
    if (!abierto) return;

    const alPresionar = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onCerrar();
    };

    document.addEventListener('keydown', alPresionar);
    const overflowPrevio = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    return () => {
      document.removeEventListener('keydown', alPresionar);
      document.body.style.overflow = overflowPrevio;
    };
  }, [abierto, onCerrar]);

  const cerrado = !abierto;
  const json = JSON.stringify(body, null, 2);
  const esExito = status >= 200 && status < 300;

  const copiar = async () => {
    try {
      await navigator.clipboard.writeText(json);
    } catch {
      /* el navegador puede bloquear el portapapeles sin HTTPS */
    }
  };

  const descargar = () => {
    const blob = new Blob([json], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'simulacion.json';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
  };

  return (
    <div
      className={cn(
        'modal-overlay fixed inset-0 z-50 flex items-center justify-center bg-black/40 p-4',
        cerrado && 'is-cerrado'
      )}
      onClick={(e) => {
        if (e.target === e.currentTarget) onCerrar();
      }}
      role="dialog"
      aria-modal="true"
      aria-hidden={cerrado || undefined}
      aria-label="Respuesta del endpoint"
    >
      <div
        className={cn(
          'modal-panel flex max-h-[80dvh] w-full max-w-2xl flex-col overflow-hidden rounded-xl border border-line bg-surface shadow-2xl',
          cerrado && 'is-cerrado'
        )}
      >
        <header className="flex items-center justify-between border-b border-line bg-[#fbfcfe] px-4 py-3">
          <div className="flex items-center gap-3 font-mono text-sm">
            <span className="rounded bg-tint px-2 py-0.5 font-semibold text-blue-d">
              {metodo}
            </span>
            <span className="text-ink">{ruta}</span>
            <span
              className={esExito ? 'font-bold text-ok' : 'font-bold text-warn'}
            >
              {status}
            </span>
          </div>
          <button
            type="button"
            onClick={onCerrar}
            className="inline-flex h-11 w-11 items-center justify-center rounded-md text-muted hover:bg-tint hover:text-ink active:bg-tint"
            aria-label="Cerrar"
          >
            ✕
          </button>
        </header>

        <pre className="flex-1 overflow-auto overscroll-contain bg-[#0f172a] p-4 font-mono text-xs leading-relaxed text-[#cbd5e1]">
          {json}
        </pre>

        <footer className="flex justify-end gap-2 border-t border-line bg-[#fbfcfe] px-4 py-3">
          <button type="button" className="btn btn-secondary" onClick={copiar}>
            Copiar JSON
          </button>
          <button type="button" className="btn btn-primary" onClick={descargar}>
            Descargar .json
          </button>
        </footer>
      </div>
    </div>
  );
}
