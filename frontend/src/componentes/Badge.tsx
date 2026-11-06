/* ===== Badge ===== */

import { cn } from '@/utils/cn';

type Variant = 'on' | 'off';

interface BadgeProps {
  children: React.ReactNode;
  variant?: Variant;
  className?: string;
}

export function Badge({ children, variant = 'off', className }: BadgeProps) {
  const base =
    'inline-block w-fit rounded-full px-3 py-1 text-[0.72rem] font-medium';
  const styles: Record<Variant, string> = {
    on: 'bg-[#dcecfb] text-[#0f548d]',
    off: 'bg-[#e9edf3] text-[#5d6d85]',
  };

  return (
    <span className={cn(base, styles[variant], className)}>{children}</span>
  );
}
