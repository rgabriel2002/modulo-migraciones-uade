/* ===== Header UADE WebCampus ===== */

import {
  IconoAyuda,
  IconoChevron,
  IconoHome,
  IconoMail,
  IconoUsuario,
  IconoWifi,
} from './Iconos';

function NavItem({
  children,
  activo = false,
}: {
  children: React.ReactNode;
  activo?: boolean;
}) {
  return (
    <button
      type="button"
      disabled
      aria-disabled="true"
      className={
        'inline-flex cursor-default items-center gap-1.5 rounded-md px-4 py-2.5 font-mont text-[0.95rem] font-medium ' +
        (activo ? 'bg-tint text-ink' : 'text-ink')
      }
    >
      {children}
    </button>
  );
}

export function Header() {
  return (
    <header className="border-b border-line bg-surface pt-[env(safe-area-inset-top)]">
      <div className="mx-auto max-w-[1600px] px-5">
        <div className="flex items-center justify-between gap-3 pt-4 pb-1.5">
          <a href="#/" aria-label="UADE WebCampus">
            <img
              src="/uade-logo.png"
              alt="UADE WebCampus"
              className="block h-20 w-auto"
            />
          </a>

          <div className="flex items-center gap-3 text-[0.95rem] text-[#2b2f36]">
            <span>Usuario de UADE</span>
            <span className="inline-flex h-14 w-14 items-end justify-center overflow-hidden rounded-full bg-[#e3e7ec]">
              <span className="mb-[-4%] block h-[76%] w-[76%] text-[#9aa3b2]">
                <IconoUsuario />
              </span>
            </span>
          </div>
        </div>

        <nav
          aria-label="Navegación principal"
          className="flex flex-wrap items-center gap-1.5 py-2"
        >
          <NavItem>
            <IconoHome className="h-[22px] w-[22px]" />
          </NavItem>
          <NavItem>
            General <IconoChevron />
          </NavItem>
          <NavItem activo>
            Académico <IconoChevron />
          </NavItem>
          <NavItem>
            Mis Cursos <IconoChevron />
          </NavItem>
          <NavItem>
            <IconoWifi className="h-[22px] w-[22px]" /> WiFi
          </NavItem>
          <NavItem>
            <IconoAyuda className="h-[22px] w-[22px]" /> Ayuda
          </NavItem>

          <span className="flex-1" />

          <button
            type="button"
            disabled
            aria-disabled="true"
            className="inline-flex cursor-default items-center gap-2 rounded-md bg-[#4a86c5] px-4 py-2.5 font-mont text-[0.95rem] font-medium text-white"
          >
            <IconoMail className="h-[22px] w-[22px]" />
            Director de Carrera
          </button>
        </nav>
      </div>
    </header>
  );
}
