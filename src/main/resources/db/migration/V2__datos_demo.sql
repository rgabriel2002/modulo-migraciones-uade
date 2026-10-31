-- Datos de demostración para probar los endpoints con el legajo 1100001.

INSERT INTO facultad (nombre, sede_ubicacion)
VALUES ('Facultad de Ingeniería y Ciencias Exactas', 'Sede Monserrat');

INSERT INTO carrera (id_facultad, nombre, titulo_otorgado, duracion_anios)
SELECT id_facultad, 'Ingeniería en Informática', 'Ingeniero/a en Informática', 5
FROM facultad
WHERE nombre = 'Facultad de Ingeniería y Ciencias Exactas';

INSERT INTO carrera (id_facultad, nombre, titulo_otorgado, duracion_anios)
SELECT id_facultad, 'Tecnicatura Universitaria en Desarrollo de Software',
       'Técnico/a Universitario/a en Desarrollo de Software', 3
FROM facultad
WHERE nombre = 'Facultad de Ingeniería y Ciencias Exactas';

INSERT INTO plan_estudio (id_carrera, codigo_plan, anio_resolucion, estado, titulo_intermedio_nombre)
SELECT id_carrera, '1621', 2021, 'ACTIVO', 'Analista en Informática'
FROM carrera
WHERE nombre = 'Ingeniería en Informática';

INSERT INTO plan_estudio (id_carrera, codigo_plan, anio_resolucion, estado, titulo_intermedio_nombre)
SELECT id_carrera, '1121', 2021, 'ACTIVO', NULL
FROM carrera
WHERE nombre = 'Tecnicatura Universitaria en Desarrollo de Software';

INSERT INTO materia (codigo, nombre, carga_horaria_total, regimen) VALUES
 ('3.4.069', 'Fundamentos de Informática',   68, 'CUATRIMESTRAL'),
 ('3.4.164', 'Sistemas de Información I',    68, 'CUATRIMESTRAL'),
 ('3.4.225', 'Introducción a la Algoritmia', 68, 'CUATRIMESTRAL'),
 ('9.9.001', 'Matemática Discreta',         102, 'CUATRIMESTRAL'),
 ('9.9.002', 'Base de Datos I',              68, 'CUATRIMESTRAL'),
 ('9.9.003', 'Programación II',              68, 'CUATRIMESTRAL'),
 ('9.9.004', 'Taller de Innovación',         68, 'CUATRIMESTRAL'),
 ('9.9.005', 'Seminario de Tendencias',      68, 'CUATRIMESTRAL'),
 ('9.9.006', 'Programación I',               68, 'CUATRIMESTRAL');

INSERT INTO plan_materia (id_plan, id_materia, anio_plan, cuatrimestre, carga_horaria, es_optativa, es_titulo_intermedio)
SELECT p.id_plan, m.id_materia, v.anio, v.cuat, v.hs, v.opt, v.ti
FROM (VALUES
  ('1621','3.4.069',1,1, 68,false,true),
  ('1621','3.4.164',1,1, 68,false,true),
  ('1621','9.9.001',1,2,102,false,true),
  ('1621','9.9.006',1,2, 68,false,true),
  ('1621','9.9.002',2,1, 68,false,true),
  ('1621','9.9.003',2,1, 68,false,true),
  ('1621','9.9.004',2,2, 68,true ,false),
  ('1121','3.4.225',1,1, 68,false,false),
  ('1121','3.4.164',1,1, 68,false,false),
  ('1121','9.9.001',1,2, 68,false,false),
  ('1121','9.9.006',1,2, 68,false,false),
  ('1121','9.9.002',2,1, 68,false,false),
  ('1121','9.9.004',2,2, 68,true ,false),
  ('1121','9.9.005',2,2, 68,true ,false)
) AS v(plan, cod, anio, cuat, hs, opt, ti)
JOIN plan_estudio p ON p.codigo_plan = v.plan
JOIN materia m ON m.codigo = v.cod;

INSERT INTO correlatividad (id_plan, id_materia, id_materia_requerida, tipo)
SELECT p.id_plan, m.id_materia, r.id_materia, v.tipo
FROM (VALUES ('9.9.003','9.9.006','FINAL'),
             ('9.9.003','3.4.069','CURSADA'),
             ('9.9.002','3.4.164','CURSADA')) AS v(mat, req, tipo)
JOIN plan_estudio p ON p.codigo_plan = '1621'
JOIN materia m ON m.codigo = v.mat
JOIN materia r ON r.codigo = v.req;

INSERT INTO equivalencia (id_plan_origen, id_plan_destino, resolucion, vigente_desde)
SELECT o.id_plan, d.id_plan, 'Res. 123/21', DATE '2021-03-01'
FROM plan_estudio o, plan_estudio d
WHERE o.codigo_plan = '1121' AND d.codigo_plan = '1621';

INSERT INTO equivalencia_materia (id_equivalencia, id_materia, rol)
SELECT e.id_equivalencia, m.id_materia, v.rol
FROM equivalencia e
JOIN (VALUES ('3.4.225','ORIGEN'), ('3.4.069','DESTINO')) AS v(cod, rol) ON true
JOIN materia m ON m.codigo = v.cod
WHERE e.resolucion = 'Res. 123/21';

INSERT INTO estudiante (legajo, dni, nombre, apellido, email, fecha_ingreso)
VALUES ('1100001', '40111222', 'Alumno', 'De Prueba',
        'alumno.prueba@uade.edu.ar', DATE '2023-03-01');

INSERT INTO estudiante_plan (id_estudiante, id_plan, estado, fecha_inscripcion)
SELECT e.id_estudiante, p.id_plan, 'ACTIVO', DATE '2023-03-01'
FROM estudiante e, plan_estudio p
WHERE e.legajo = '1100001' AND p.codigo_plan = '1121';

INSERT INTO historial_academico (id_estudiante, id_materia, estado_global, nota_final, fecha_aprobacion)
SELECT e.id_estudiante, m.id_materia, v.estado, v.nota, v.fecha
FROM estudiante e
JOIN (VALUES ('3.4.225','APROBADA', 8.00, DATE '2023-07-10'),
             ('3.4.164','APROBADA', 7.00, DATE '2023-07-15'),
             ('9.9.001','APROBADA', 6.00, DATE '2023-12-01'),
             ('9.9.002','FINAL',    NULL, NULL),
             ('9.9.006','CURSANDO', NULL, NULL),
             ('9.9.004','APROBADA', 9.00, DATE '2024-12-10'),
             ('9.9.005','APROBADA', 8.00, DATE '2024-12-12'))
     AS v(cod, estado, nota, fecha) ON true
JOIN materia m ON m.codigo = v.cod
WHERE e.legajo = '1100001';

INSERT INTO detalle_cursada (id_historial, anio_cursada, cuatrimestre, condicion_alcanzada, nota_cierre_cursada)
SELECT h.id_historial, v.anio, v.cuat, v.cond, v.nota
FROM historial_academico h
JOIN materia m ON m.id_materia = h.id_materia
JOIN (VALUES ('9.9.002', 2024, 1, 'LIBRE',   3.00),
             ('9.9.002', 2024, 2, 'REGULAR', 6.00),
             ('3.4.225', 2023, 1, 'PROMOCIONADA', 8.00)) AS v(cod, anio, cuat, cond, nota)
  ON v.cod = m.codigo;
