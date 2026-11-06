-- Datos demo para multi-carrera en origen y filtro por facultad.
-- Agrega la Facultad de Ciencias Económicas y un segundo estudiante (1100002)
-- inscripto en dos carreras a la vez: Ingeniería en Informática y Contador Público.

INSERT INTO facultad (nombre, sede_ubicacion)
VALUES ('Facultad de Ciencias Económicas', 'Sede Monserrat');

INSERT INTO carrera (id_facultad, nombre, titulo_otorgado, duracion_anios)
SELECT id_facultad, 'Contador Público', 'Contador/a Público/a', 4
FROM facultad
WHERE nombre = 'Facultad de Ciencias Económicas';

INSERT INTO carrera (id_facultad, nombre, titulo_otorgado, duracion_anios)
SELECT id_facultad, 'Licenciatura en Administración', 'Licenciado/a en Administración', 4
FROM facultad
WHERE nombre = 'Facultad de Ciencias Económicas';

INSERT INTO plan_estudio (id_carrera, codigo_plan, anio_resolucion, estado, titulo_intermedio_nombre)
SELECT id_carrera, '5010', 2022, 'ACTIVO', NULL
FROM carrera
WHERE nombre = 'Contador Público';

INSERT INTO plan_estudio (id_carrera, codigo_plan, anio_resolucion, estado, titulo_intermedio_nombre)
SELECT id_carrera, '5020', 2022, 'ACTIVO', NULL
FROM carrera
WHERE nombre = 'Licenciatura en Administración';

INSERT INTO materia (codigo, nombre, carga_horaria_total, regimen) VALUES
  ('5.1.001', 'Contabilidad Básica',               68, 'CUATRIMESTRAL'),
  ('5.1.002', 'Administración General',            68, 'CUATRIMESTRAL'),
  ('5.1.003', 'Economía',                          68, 'CUATRIMESTRAL'),
  ('5.1.004', 'Matemática Aplicada',               68, 'CUATRIMESTRAL'),
  ('5.1.005', 'Impuestos I',                       68, 'CUATRIMESTRAL'),
  ('5.1.006', 'Derecho Empresario',                68, 'CUATRIMESTRAL'),
  ('5.1.007', 'Recursos Humanos',                  68, 'CUATRIMESTRAL'),
  ('5.1.008', 'Sistemas de Información Contable',  68, 'CUATRIMESTRAL');

INSERT INTO plan_materia (id_plan, id_materia, anio_plan, cuatrimestre, carga_horaria, es_optativa, es_titulo_intermedio)
SELECT p.id_plan, m.id_materia, v.anio, v.cuat, v.hs, v.opt, v.ti
FROM (VALUES
  ('5010','5.1.001',1,1, 68,false,false),
  ('5010','5.1.002',1,1, 68,false,false),
  ('5010','9.9.001',1,2,102,false,false),
  ('5010','5.1.003',1,2, 68,false,false),
  ('5010','5.1.004',2,1, 68,false,false),
  ('5010','5.1.005',2,1, 68,false,false),
  ('5010','5.1.006',2,2, 68,false,false),
  ('5010','5.1.008',2,2, 68,false,false),
  ('5020','5.1.002',1,1, 68,false,false),
  ('5020','9.9.001',1,2,102,false,false),
  ('5020','5.1.003',1,2, 68,false,false),
  ('5020','5.1.006',2,1, 68,false,false),
  ('5020','5.1.007',2,2, 68,false,false)
) AS v(plan, cod, anio, cuat, hs, opt, ti)
JOIN plan_estudio p ON p.codigo_plan = v.plan
JOIN materia m ON m.codigo = v.cod;

-- Equivalencia entre facultades: Ingeniería -> Económicas.
INSERT INTO equivalencia (id_plan_origen, id_plan_destino, resolucion, vigente_desde)
SELECT o.id_plan, d.id_plan, 'Res. 456/22', DATE '2022-06-01'
FROM plan_estudio o, plan_estudio d
WHERE o.codigo_plan = '1621' AND d.codigo_plan = '5010';

INSERT INTO equivalencia_materia (id_equivalencia, id_materia, rol)
SELECT e.id_equivalencia, m.id_materia, v.rol
FROM equivalencia e
JOIN (VALUES ('3.4.164','ORIGEN'), ('5.1.008','DESTINO')) AS v(cod, rol) ON true
JOIN materia m ON m.codigo = v.cod
WHERE e.resolucion = 'Res. 456/22';

-- Segundo estudiante con dos carreras activas a la vez.
INSERT INTO estudiante (legajo, dni, nombre, apellido, email, fecha_ingreso)
VALUES ('1100002', '40555666', 'Alumna', 'Multi Carrera',
        'alumna.multi@uade.edu.ar', DATE '2022-03-01');

INSERT INTO estudiante_plan (id_estudiante, id_plan, estado, fecha_inscripcion)
SELECT e.id_estudiante, p.id_plan, 'ACTIVO', DATE '2022-03-01'
FROM estudiante e, plan_estudio p
WHERE e.legajo = '1100002' AND p.codigo_plan IN ('1621', '5010');

INSERT INTO historial_academico (id_estudiante, id_materia, estado_global, nota_final, fecha_aprobacion)
SELECT e.id_estudiante, m.id_materia, v.estado, v.nota, v.fecha
FROM estudiante e
JOIN (VALUES ('9.9.001','APROBADA', 7.00, DATE '2023-12-01'),
             ('3.4.164','APROBADA', 8.00, DATE '2023-07-15'),
             ('9.9.006','APROBADA', 6.00, DATE '2023-07-10'),
             ('5.1.001','APROBADA', 9.00, DATE '2024-07-10'),
             ('5.1.002','CURSANDO', NULL, NULL))
     AS v(cod, estado, nota, fecha) ON true
JOIN materia m ON m.codigo = v.cod
WHERE e.legajo = '1100002';
