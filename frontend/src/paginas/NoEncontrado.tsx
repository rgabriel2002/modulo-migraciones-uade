/* ===== Página no encontrada ===== */

import { useNavigate } from 'react-router-dom';
import { Boton } from '@/componentes/Boton';

export function NoEncontrado() {
  const navigate = useNavigate();

  return (
    <div className="mx-auto max-w-[1280px] px-4 py-16 text-center sm:px-8">
      <h1 className="mb-2 font-sans text-[1.6rem] font-bold text-ink">
        Página no encontrada
      </h1>
      <p className="mb-6 text-[0.92rem] text-muted">
        La dirección que ingresaste no existe.
      </p>
      <Boton onClick={() => navigate('/')}>Volver al inicio</Boton>
    </div>
  );
}
