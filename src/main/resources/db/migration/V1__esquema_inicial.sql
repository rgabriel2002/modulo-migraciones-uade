CREATE TABLE facultad (
    id_facultad     SERIAL       PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    sede_ubicacion  VARCHAR(100)
);

CREATE TABLE carrera (
    id_carrera       SERIAL       PRIMARY KEY,
    id_facultad      INT          NOT NULL,
    nombre           VARCHAR(150) NOT NULL,
    titulo_otorgado  VARCHAR(150),
    duracion_anios   SMALLINT     CHECK (duracion_anios > 0),
    CONSTRAINT fk_carrera_facultad FOREIGN KEY (id_facultad)
        REFERENCES facultad (id_facultad) ON DELETE RESTRICT
);

CREATE TABLE plan_estudio (
    id_plan                   SERIAL       PRIMARY KEY,
    id_carrera                INT          NOT NULL,
    codigo_plan               VARCHAR(20)  NOT NULL UNIQUE,
    anio_resolucion           SMALLINT     NOT NULL,
    estado                    VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    titulo_intermedio_nombre  VARCHAR(150),
    CONSTRAINT ck_plan_estado CHECK (estado IN ('ACTIVO', 'INACTIVO')),
    CONSTRAINT fk_plan_carrera FOREIGN KEY (id_carrera)
        REFERENCES carrera (id_carrera) ON DELETE RESTRICT
);

CREATE TABLE materia (
    id_materia           SERIAL       PRIMARY KEY,
    codigo               VARCHAR(20)  NOT NULL UNIQUE,
    nombre               VARCHAR(150) NOT NULL,
    carga_horaria_total  INT          CHECK (carga_horaria_total > 0),
    regimen              VARCHAR(20),
    CONSTRAINT ck_materia_regimen CHECK (regimen IN ('CUATRIMESTRAL', 'ANUAL'))
);

CREATE TABLE plan_materia (
    id_plan               INT       NOT NULL,
    id_materia            INT       NOT NULL,
    anio_plan             SMALLINT  NOT NULL,
    cuatrimestre          SMALLINT,                              -- NULL si es anual
    carga_horaria         INT       NOT NULL,
    es_optativa           BOOLEAN   NOT NULL DEFAULT FALSE,
    es_titulo_intermedio  BOOLEAN   NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id_plan, id_materia),
    CONSTRAINT ck_pm_anio         CHECK (anio_plan BETWEEN 1 AND 7),
    CONSTRAINT ck_pm_cuatrimestre CHECK (cuatrimestre IN (1, 2)),
    CONSTRAINT ck_pm_carga        CHECK (carga_horaria > 0),
    CONSTRAINT fk_pm_plan    FOREIGN KEY (id_plan)    REFERENCES plan_estudio (id_plan) ON DELETE CASCADE,
    CONSTRAINT fk_pm_materia FOREIGN KEY (id_materia) REFERENCES materia (id_materia)    ON DELETE RESTRICT
);

CREATE TABLE correlatividad (
    id_plan               INT          NOT NULL,
    id_materia            INT          NOT NULL,
    id_materia_requerida  INT          NOT NULL,
    tipo                  VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id_plan, id_materia, id_materia_requerida),
    CONSTRAINT ck_corr_tipo      CHECK (tipo IN ('CURSADA', 'FINAL')),
    CONSTRAINT ck_corr_distintas CHECK (id_materia <> id_materia_requerida),
    CONSTRAINT fk_corr_materia   FOREIGN KEY (id_plan, id_materia)
        REFERENCES plan_materia (id_plan, id_materia) ON DELETE CASCADE,
    CONSTRAINT fk_corr_requerida FOREIGN KEY (id_plan, id_materia_requerida)
        REFERENCES plan_materia (id_plan, id_materia) ON DELETE CASCADE
);

CREATE TABLE equivalencia (
    id_equivalencia  SERIAL       PRIMARY KEY,
    id_plan_origen   INT          NOT NULL,
    id_plan_destino  INT          NOT NULL,
    resolucion       VARCHAR(50)  NOT NULL,
    vigente_desde    DATE         NOT NULL,
    vigente_hasta    DATE,                                       -- NULL = vigente
    CONSTRAINT ck_eq_planes   CHECK (id_plan_origen <> id_plan_destino),
    CONSTRAINT ck_eq_vigencia CHECK (vigente_hasta IS NULL OR vigente_hasta >= vigente_desde),
    CONSTRAINT fk_eq_origen  FOREIGN KEY (id_plan_origen)  REFERENCES plan_estudio (id_plan) ON DELETE RESTRICT,
    CONSTRAINT fk_eq_destino FOREIGN KEY (id_plan_destino) REFERENCES plan_estudio (id_plan) ON DELETE RESTRICT
);

CREATE TABLE equivalencia_materia (
    id_equivalencia  INT          NOT NULL,
    id_materia       INT          NOT NULL,
    rol              VARCHAR(10)  NOT NULL,
    PRIMARY KEY (id_equivalencia, id_materia, rol),
    CONSTRAINT ck_em_rol CHECK (rol IN ('ORIGEN', 'DESTINO')),
    CONSTRAINT fk_em_equivalencia FOREIGN KEY (id_equivalencia)
        REFERENCES equivalencia (id_equivalencia) ON DELETE CASCADE,
    CONSTRAINT fk_em_materia FOREIGN KEY (id_materia)
        REFERENCES materia (id_materia) ON DELETE RESTRICT
);

CREATE TABLE estudiante (
    id_estudiante  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    legajo         VARCHAR(50)   NOT NULL UNIQUE,
    dni            VARCHAR(15)   UNIQUE,
    nombre         VARCHAR(100)  NOT NULL,
    apellido       VARCHAR(100)  NOT NULL,
    email          VARCHAR(150),
    fecha_ingreso  DATE
);

CREATE TABLE estudiante_plan (
    id_estudiante      UUID         NOT NULL,
    id_plan            INT          NOT NULL,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO',
    fecha_inscripcion  DATE,
    PRIMARY KEY (id_estudiante, id_plan),
    CONSTRAINT ck_ep_estado CHECK (estado IN ('ACTIVO', 'MIGRADO', 'FINALIZADO', 'ABANDONADO')),
    CONSTRAINT fk_ep_estudiante FOREIGN KEY (id_estudiante) REFERENCES estudiante (id_estudiante) ON DELETE CASCADE,
    CONSTRAINT fk_ep_plan       FOREIGN KEY (id_plan)       REFERENCES plan_estudio (id_plan)     ON DELETE RESTRICT
);

CREATE TABLE historial_academico (
    id_historial      UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    id_estudiante     UUID          NOT NULL,
    id_materia        INT           NOT NULL,
    estado_global     VARCHAR(20)   NOT NULL,
    nota_final        NUMERIC(4,2),
    fecha_aprobacion  DATE,
    CONSTRAINT uq_historial_alumno_materia UNIQUE (id_estudiante, id_materia),
    CONSTRAINT ck_hist_estado CHECK (estado_global IN ('APROBADA', 'CURSANDO', 'FINAL')),
    CONSTRAINT ck_hist_nota   CHECK (nota_final BETWEEN 0 AND 10),
    CONSTRAINT ck_hist_fecha  CHECK (estado_global = 'APROBADA' OR fecha_aprobacion IS NULL),
    CONSTRAINT fk_historial_estudiante FOREIGN KEY (id_estudiante)
        REFERENCES estudiante (id_estudiante) ON DELETE CASCADE,
    CONSTRAINT fk_historial_materia FOREIGN KEY (id_materia)
        REFERENCES materia (id_materia) ON DELETE RESTRICT
);

CREATE TABLE detalle_cursada (
    id_detalle           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    id_historial         UUID          NOT NULL,
    anio_cursada         SMALLINT      NOT NULL,
    cuatrimestre         SMALLINT,
    condicion_alcanzada  VARCHAR(20),
    nota_cierre_cursada  NUMERIC(4,2),
    CONSTRAINT ck_dc_cuatrimestre CHECK (cuatrimestre IN (1, 2)),
    CONSTRAINT ck_dc_condicion    CHECK (condicion_alcanzada IN ('PROMOCIONADA', 'REGULAR', 'LIBRE')),
    CONSTRAINT ck_dc_nota         CHECK (nota_cierre_cursada BETWEEN 0 AND 10),
    CONSTRAINT fk_detalle_historial FOREIGN KEY (id_historial)
        REFERENCES historial_academico (id_historial) ON DELETE CASCADE
);

CREATE INDEX idx_carrera_facultad      ON carrera (id_facultad);
CREATE INDEX idx_plan_carrera          ON plan_estudio (id_carrera);
CREATE INDEX idx_pm_materia            ON plan_materia (id_materia);
CREATE INDEX idx_corr_requerida        ON correlatividad (id_plan, id_materia_requerida);
CREATE INDEX idx_eq_planes             ON equivalencia (id_plan_origen, id_plan_destino);
CREATE INDEX idx_em_materia            ON equivalencia_materia (id_materia);
CREATE INDEX idx_ep_plan               ON estudiante_plan (id_plan);
CREATE INDEX idx_historial_materia     ON historial_academico (id_materia);
CREATE INDEX idx_detalle_historial     ON detalle_cursada (id_historial);

-- Una sola materia DESTINO por equivalencia
CREATE UNIQUE INDEX uq_em_un_destino   ON equivalencia_materia (id_equivalencia) WHERE rol = 'DESTINO';
