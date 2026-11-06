/* ===== Footer ===== */

import { IconoAyuda } from './Iconos';

export function Footer() {
  return (
    <footer className="mx-auto flex w-full max-w-[1280px] flex-wrap items-center justify-between gap-3 px-4 pt-6 pb-[calc(2rem_+_env(safe-area-inset-bottom))] text-[0.78rem] text-muted sm:px-8">
      <p className="m-0">
        La simulación es una consulta; no modifica tu inscripción ni tu plan de
        estudios.
      </p>
      <button
        type="button"
        disabled
        aria-disabled="true"
        className="inline-flex cursor-default items-center gap-1.5 text-blue"
      >
        <IconoAyuda className="h-[15px] w-[15px]" />
        Ayuda con la simulación
      </button>
    </footer>
  );
}
