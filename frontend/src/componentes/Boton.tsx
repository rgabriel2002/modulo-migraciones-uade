/* ===== Boton ===== */

import { cn } from '@/utils/cn';

type Variant = 'primario' | 'secundario' | 'texto' | 'deshabilitado';

interface BotonProps {
  children: React.ReactNode;
  variant?: Variant;
  disabled?: boolean;
  onClick?: () => void;
  className?: string;
  type?: 'button' | 'submit' | 'reset';
  title?: string;
}

export function Boton({
  children,
  variant = 'primario',
  disabled = false,
  onClick,
  className,
  type = 'button',
  title,
}: BotonProps) {
  const base =
    'inline-flex items-center justify-center gap-2 h-[42px] px-5 rounded-lg border text-[0.88rem] font-semibold transition-[background-color,border-color,color,transform,scale] duration-150 ease-out active:scale-[0.97] motion-reduce:active:scale-100 disabled:cursor-not-allowed disabled:opacity-60';

  const styles: Record<Variant, string> = {
    primario: 'bg-blue text-white border-blue hover:bg-blue-d',
    secundario: 'bg-surface text-blue-d border-[#b9cde3] hover:bg-tint',
    texto:
      'bg-transparent border-transparent text-blue underline underline-offset-4 hover:bg-tint',
    deshabilitado: 'bg-[#e9edf3] text-[#8a97aa] border-[#d6dde7]',
  };

  return (
    <button
      type={type}
      className={cn(
        base,
        disabled ? styles.deshabilitado : styles[variant],
        className
      )}
      disabled={disabled}
      onClick={onClick}
      title={title}
    >
      {children}
    </button>
  );
}
