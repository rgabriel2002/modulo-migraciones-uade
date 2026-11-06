/* ===== KPIs del resultado ===== */

interface KpiProps {
  valor: number | string;
  etiqueta: string;
}

interface KpisProps {
  kpis: KpiProps[];
}

export function Kpis({ kpis }: KpisProps) {
  return (
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-4 md:gap-4">
      {kpis.map(({ valor, etiqueta }, i) => (
        <div key={i} className="card p-3.5 md:p-4">
          <div className="text-xl font-bold text-blue-d tabular-nums md:text-2xl">{valor}</div>
          <div className="text-[0.8rem] text-muted mt-1 md:text-[0.875rem]">{etiqueta}</div>
        </div>
      ))}
    </div>
  );
}
