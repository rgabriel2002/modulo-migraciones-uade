/* ===== Utilitario para unir clases CSS (clsx + tailwind-merge ligero) ===== */

export function cn(...classes: (string | undefined | null | boolean)[]): string {
  return classes.filter(Boolean).join(' ');
}
