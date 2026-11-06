/* ===== Banner informativo ===== */

import { cn } from '@/utils/cn';

type Variant = 'info' | 'warn' | 'ok';

interface BannerProps {
  children: React.ReactNode;
  variant?: Variant;
  icono?: React.ReactNode;
  className?: string;
}

export function Banner({
  children,
  variant = 'info',
  icono,
  className,
}: BannerProps) {
  const base =
    'border rounded-lg p-4 mb-4 flex gap-3 items-start';
  const styles: Record<Variant, string> = {
    info: 'bg-[#eaf2fb] border-[#d5e5f6] text-[#14284b]',
    warn: 'bg-[#fff8e1] border-[#f1e2a8] text-[#6b5414]',
    ok: 'bg-[#eaf6ef] border-[#cfe8d9] text-[#2e6a3e]',
  };

  return (
    <div className={cn(base, styles[variant], className)}>
      {icono && <span className="mt-0.5">{icono}</span>}
      <span>{children}</span>
    </div>
  );
}
