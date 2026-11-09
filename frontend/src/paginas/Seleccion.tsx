/* ===== Pantalla de selección de planes ===== */

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useLegajo } from '@/hooks/useLegajo';
import { useSituacion } from '@/hooks/useSituacion';
import { usePlanes } from '@/hooks/usePlanes';
import { ApiError } from '@/api/cliente';
import { Badge } from '@/componentes/Badge';
import { Banner } from '@/componentes/Banner';
import { Boton } from '@/componentes/Boton';
import { CajaPlan } from '@/componentes/CajaPlan';
import { SelectorCarrera } from '@/componentes/SelectorCarrera';
import { SelectorFacultad } from '@/componentes/SelectorFacultad';
import { SelectorOrigen } from '@/componentes/SelectorOrigen';
import { SelectorPlan } from '@/componentes/SelectorPlan';
import {
  IconoBirrete,
  IconoDocumento,
  IconoFlecha,
  IconoImpresora,
  IconoInfo,
  IconoLibro,
  IconoLupa,
} from '@/componentes/Iconos';

function mensajeDeError(error: unknown): string | null {
  if (!error) return null;
  if (error instanceof ApiError) return error.message;
  return 'Ocurrió un error inesperado.';
}

export function Seleccion() {
  const navigate = useNavigate();
  const { legajo, guardar, formatoValido } = useLegajo();

  const [input, setInput] = useState(legajo ?? '');
  const [errorLocal, setErrorLocal] = useState<string | null>(null);
  const [planOrigen, setPlanOrigen] = useState<string | null>(null);
  const [facultad, setFacultad] = useState<string | null>(null);
  const [carrera, setCarrera] = useState<string | null>(null);
  const [planDestino, setPlanDestino] = useState<string | null>(null);

  const situacion = useSituacion(legajo);

  const planesActivos = situacion.data?.body?.planesActivos ?? [];

  // Origen: si hay un solo plan activo queda fijo; con varios se elige.
  const origenEfectivo =
    planOrigen && planesActivos.some((p) => p.codigo === planOrigen)
      ? planOrigen
      : planesActivos.length === 1
        ? planesActivos[0].codigo
        : null;
  const origenElegido =
    planesActivos.find((p) => p.codigo === origenEfectivo) ?? null;

  const planes = usePlanes(legajo, origenEfectivo);

  const listaPlanes = planes.data?.body ?? [];

  // Destino: cascada facultad → carrera → plan, con preselección cuando
  // hay una sola opción en el nivel.
  const facultades = Array.from(new Set(listaPlanes.map((p) => p.facultad)));
  const facultadEfectiva =
    facultad ?? (facultades.length === 1 ? facultades[0] : null);
  const carrerasDeFacultad = facultadEfectiva
    ? Array.from(
        new Set(
          listaPlanes
            .filter((p) => p.facultad === facultadEfectiva)
            .map((p) => p.carrera)
        )
      )
    : [];
  const carreraEfectiva =
    carrera ?? (carrerasDeFacultad.length === 1 ? carrerasDeFacultad[0] : null);

  const confirmarLegajo = () => {
    const limpio = input.trim();
    if (!formatoValido.test(limpio)) {
      setErrorLocal('Ingresá un legajo de 7 a 15 dígitos.');
      return;
    }
    setErrorLocal(null);
    setPlanOrigen(null);
    setFacultad(null);
    setCarrera(null);
    setPlanDestino(null);
    guardar(limpio);
  };

  const error =
    errorLocal ??
    mensajeDeError(situacion.error) ??
    mensajeDeError(planes.error);

  const cargando = situacion.isFetching || planes.isFetching;
  const listo = !!legajo && planesActivos.length > 0 && listaPlanes.length > 0;
  const puedeBuscar = listo && !!origenEfectivo && !!planDestino && !cargando;

  const buscar = () => {
    if (!legajo || !origenEfectivo || !planDestino) return;
    navigate(`/simulacion/${legajo}/${origenEfectivo}/${planDestino}`);
  };

  return (
    <div className="mx-auto max-w-[1280px] px-4 pt-6 pb-7 sm:px-8 sm:pt-9">
      <h1 className="mb-2 font-sans text-[clamp(1.45rem,2.4vw,1.95rem)] font-bold tracking-[-0.01em] text-ink">
        Simulación de Migración de Plan de Estudios
      </h1>
      <p className="mb-7 text-[0.92rem] text-muted">
        Completá el siguiente formulario con la carrera y el plan de origen y
        destino para simular la migración.
      </p>

      <section className="card" aria-label="Selección de planes">
        {/* Legajo */}
        <div className="grid grid-cols-1 items-end gap-2 border-b border-line pb-6 md:grid-cols-[minmax(220px,300px)_1fr_auto] md:gap-5">
          <div>
            <label className="label" htmlFor="legajo">
              Legajo
            </label>
            <div className="relative">
              <input
                id="legajo"
                className="field pr-10"
                inputMode="numeric"
                maxLength={15}
                autoComplete="off"
                autoCorrect="off"
                autoCapitalize="none"
                spellCheck={false}
                enterKeyHint="search"
                placeholder="Ingresá el número de legajo"
                value={input}
                onChange={(e) => setInput(e.target.value.replace(/\D/g, ''))}
                onBlur={() => {
                  const limpio = input.trim();
                  if (formatoValido.test(limpio) && limpio !== legajo) {
                    confirmarLegajo();
                  }
                }}
                onKeyDown={(e) => {
                  if (e.key === 'Enter') confirmarLegajo();
                }}
              />
              <button
                type="button"
                onClick={confirmarLegajo}
                aria-label="Buscar legajo"
                className="absolute top-0 right-0 flex h-[42px] w-10 items-center justify-center text-blue active:opacity-60"
              >
                <IconoLupa className="h-[18px] w-[18px]" />
              </button>
            </div>
          </div>

          <p className="m-0 text-[0.78rem] text-muted">
            Con este legajo se consulta tu situación académica y tus planes
            disponibles. En producción se toma de la sesión del alumno.
          </p>

          <Badge variant={legajo ? 'on' : 'off'}>
            {legajo ? `Legajo ${legajo}` : 'Sin selección'}
          </Badge>
        </div>

        {error && (
          <Banner variant="warn" className="banner-entrada mt-5" icono={<IconoInfo />}>
            {error}
          </Banner>
        )}

        {/* Origen / Destino */}
        <div className="mt-5 grid grid-cols-1 items-center gap-5 lg:grid-cols-[1fr_auto_1fr]">
          <CajaPlan
            titulo="Origen"
            subtitulo="Carrera y plan actuales"
            icono={<IconoLibro />}
          >
            {planesActivos.length > 1 && (
              <SelectorOrigen
                planes={planesActivos}
                value={origenEfectivo}
                onChange={(codigo) => {
                  setPlanOrigen(codigo);
                  setFacultad(null);
                  setCarrera(null);
                  setPlanDestino(null);
                }}
                disabled={!legajo || cargando}
              />
            )}
            <div>
              <label className="label" htmlFor="origen-facultad">
                Facultad
              </label>
              <input
                id="origen-facultad"
                className="field"
                value={origenElegido?.facultad ?? ''}
                placeholder={
                  cargando ? 'Consultando…' : 'Ingresá tu legajo'
                }
                disabled
                readOnly
              />
            </div>
            {planesActivos.length <= 1 && (
              <div>
                <label className="label" htmlFor="origen-carrera">
                  Carrera
                </label>
                <input
                  id="origen-carrera"
                  className="field"
                  value={origenElegido?.carrera ?? ''}
                  placeholder={
                    cargando ? 'Consultando…' : 'Ingresá tu legajo'
                  }
                  disabled
                  readOnly
                />
              </div>
            )}
            <div>
              <label className="label" htmlFor="origen-plan">
                Plan
              </label>
              <input
                id="origen-plan"
                className="field"
                value={origenElegido ? `Plan ${origenElegido.codigo}` : ''}
                placeholder={
                  cargando ? 'Consultando…' : 'Ingresá tu legajo'
                }
                disabled
                readOnly
              />
            </div>
          </CajaPlan>

          <div
            aria-hidden="true"
            className="mx-auto flex h-[38px] w-[38px] items-center justify-center rounded-full bg-tint text-blue"
          >
            <IconoFlecha className="h-[18px] w-[18px]" />
          </div>

          <CajaPlan
            titulo="Destino"
            subtitulo="Carrera y plan a los que querés migrar"
            icono={<IconoBirrete />}
          >
            <SelectorFacultad
              planes={listaPlanes}
              facultad={facultadEfectiva}
              onChange={(nueva) => {
                setFacultad(nueva || null);
                setCarrera(null);
                setPlanDestino(null);
              }}
              disabled={!listo || cargando}
            />
            <SelectorCarrera
              planes={listaPlanes}
              facultad={facultadEfectiva}
              carrera={carreraEfectiva}
              onChange={(nueva) => {
                setCarrera(nueva || null);
                setPlanDestino(null);
              }}
              disabled={!listo || cargando}
            />
            <SelectorPlan
              planes={listaPlanes}
              carrera={carreraEfectiva}
              value={planDestino}
              onChange={setPlanDestino}
              disabled={!listo || cargando}
            />
          </CajaPlan>
        </div>

        {/* Acciones */}
        <div className="mt-6 flex flex-wrap items-center justify-between gap-4">
          <div className="flex items-center gap-2 text-[0.78rem] text-muted">
            <IconoInfo className="h-[15px] w-[15px]" />
            {listo
              ? 'Elegí el plan destino y presioná Buscar.'
              : 'Seleccioná ambos planes para realizar la búsqueda.'}
          </div>

          <div className="flex flex-wrap gap-3">
            <Boton
              variant="secundario"
              disabled
              title="Disponible después de simular"
            >
              <IconoImpresora className="h-4 w-4" />
              Descargar PDF
            </Boton>
            <Boton onClick={buscar} disabled={!puedeBuscar}>
              <IconoLupa className="h-4 w-4" />
              Buscar
            </Boton>
          </div>
        </div>
      </section>

      <div className="mt-6">
        <Banner icono={<IconoDocumento />}>
          <strong className="block text-[0.92rem]">
            Tu simulación comienza con la selección de los planes
          </strong>
          <span className="mt-0.5 block text-[0.8rem] text-muted">
            Elegí origen y destino y presioná Buscar: vas a ver tu plan de
            carrera con las equivalencias y el detalle de cada materia.
          </span>
        </Banner>
      </div>
    </div>
  );
}
