/* ===== Toggle de datos de peor caso (solo desarrollo) =====
   Cambia la data, no el componente: Resultado recibe la respuesta desde
   el fixture elegido. Se monta solo con import.meta.env.DEV y la
   selección persiste en la URL (?data=peor|vacio|uno).
*/

import type { ModoDatos } from '@/api/fixtures';
import { MODOS } from './modoDatos';

interface ToggleDatosProps {
  modo: ModoDatos;
  onCambiar: (modo: ModoDatos) => void;
}

export function ToggleDatos({ modo, onCambiar }: ToggleDatosProps) {
  return (
    <div
      className="fixed inset-x-0 bottom-[calc(0.75rem_+_env(safe-area-inset-bottom))] z-40 flex justify-center px-3"
      data-dev-only="true"
    >
      <div
        role="group"
        aria-label="Datos de prueba"
        className="flex items-center gap-1 rounded-full border border-[#c9d2df] bg-[#eef1f5] p-1 font-sans shadow-lg"
      >
        <span className="px-2 text-[11px] font-semibold tracking-wide text-[#5d6d85] uppercase">
          Datos
        </span>
        {MODOS.map(({ id, etiqueta }) => (
          <button
            key={id}
            type="button"
            onClick={() => onCambiar(id)}
            aria-pressed={modo === id}
            className={
              'rounded-full px-2.5 py-1 text-[11px] font-medium transition-colors ' +
              (modo === id
                ? 'bg-white text-[#14284b] shadow-sm'
                : 'text-[#5d6d85] hover:text-[#14284b]')
            }
          >
            {etiqueta}
          </button>
        ))}
      </div>
    </div>
  );
}
