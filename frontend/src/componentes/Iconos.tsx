/* ===== Íconos SVG inline ===== */

type IconProps = {
  className?: string;
};

const base = 'h-5 w-5';

export function IconoHome({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 3 2 11h3v9h6v-6h2v6h6v-9h3L12 3z" />
    </svg>
  );
}

export function IconoChevron({ className }: IconProps) {
  return (
    <svg className={className ?? 'h-3 w-3'} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" aria-hidden="true">
      <path d="m6 9 6 6 6-6" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

export function IconoWifi({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 20a2 2 0 1 0 0-4 2 2 0 0 0 0 4zm-4.2-5.2a6 6 0 0 1 8.4 0l1.4-1.4a8 8 0 0 0-11.2 0l1.4 1.4zM4.4 11.4a11 11 0 0 1 15.2 0L21 10a13 13 0 0 0-18 0l1.4 1.4z" />
    </svg>
  );
}

export function IconoAyuda({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm1 15h-2v-2h2v2zm1.1-6.4-.9.9c-.7.7-1.2 1.3-1.2 2.5h-2c0-1.7.7-2.5 1.5-3.3l1-1a1.8 1.8 0 0 0-1.3-3.1A1.8 1.8 0 0 0 9.4 9h-2a3.8 3.8 0 1 1 6.7 2.6z" />
    </svg>
  );
}

export function IconoMail({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M20 4H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V6a2 2 0 0 0-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z" />
    </svg>
  );
}

export function IconoUsuario({ className }: IconProps) {
  return (
    <svg className={className ?? 'h-full w-full'} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 12a4.5 4.5 0 1 0 0-9 4.5 4.5 0 0 0 0 9zm0 2c-4 0-8 2-8 5v2h16v-2c0-3-4-5-8-5z" />
    </svg>
  );
}

export function IconoLupa({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <circle cx="11" cy="11" r="7" />
      <path d="m20 20-3.5-3.5" strokeLinecap="round" />
    </svg>
  );
}

export function IconoLibro({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M11 5.5C9.5 4.5 7 4 3 4v14c4 0 6.5.5 8 1.5v-14zm2 0v14c1.5-1 4-1.5 8-1.5V4c-4 0-6.5.5-8 1.5z" />
    </svg>
  );
}

export function IconoBirrete({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 3 1 8l11 5 9-4.1V15h2V8L12 3zM5 12.2V16c0 1.7 3.1 3 7 3s7-1.3 7-3v-3.8l-7 3.2-7-3.2z" />
    </svg>
  );
}

export function IconoFlecha({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M5 12h14m-6-6 6 6-6 6" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

export function IconoInfo({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z" />
    </svg>
  );
}

export function IconoDocumento({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8l-6-6zm-1 9v4h-2v-4H8.5L12 7.5 15.5 11H13zm1-7.5L18.5 9H14V3.5z" />
    </svg>
  );
}

export function IconoImpresora({ className }: IconProps) {
  return (
    <svg className={className ?? base} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M6 9V2h12v7" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2" strokeLinecap="round" strokeLinejoin="round" />
      <rect x="6" y="14" width="12" height="8" rx="1" />
    </svg>
  );
}

