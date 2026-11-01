# Arquitectura del Simulador de Migración

## Diagrama de capas

```mermaid
flowchart TD
    subgraph CLIENTE [Cliente]
        C[curl / IntelliJ api.http / Frontend]
    end

    subgraph API [Capa API]
        C1[PlanController]
        C2[EstudianteController]
        C3[SimulacionController]
        C4[EquivalenciaController]
    end

    subgraph SERVICIO [Capa Servicio]
        S1[PlanService]
        S2[EstudianteService]
        S3[SimulacionService]
        S4[EquivalenciaService]
        M[SimulacionMapper]
    end

    subgraph MOTOR [Motor de Simulación]
        MS[MotorSimulacion]
        R1[ReglaMismaMateria]
        R2[ReglaEquivalenciaPorResolucion]
    end

    subgraph INTEGRACION [Integración / Adaptador]
        F[FuenteSituacionAcademicaLocal]
    end

    subgraph REPOS [Repositorios JPA]
        RP[PlanEstudioRepository]
        ER[EquivalenciaRepository]
        SR[EstudianteRepository]
        HR[HistorialAcademicoRepository]
        OTROS[Otros repositorios]
    end

    subgraph DB[PostgreSQL 16]
        DB1[Tablas del esquema V1]
        DB2[Datos demo V2]
    end

    C --> C1
    C --> C2
    C --> C3
    C --> C4

    C1 --> S1
    C2 --> S2
    C3 --> S3
    C4 --> S4

    S1 --> RP
    S2 --> F
    S3 --> F
    S3 --> RP
    S3 --> ER
    S4 --> ER

    S1 --> M
    S2 --> M
    S3 --> M
    S4 --> M

    S3 --> MS
    MS --> R1
    MS --> R2

    F --> SR
    F --> HR
    F --> RP

    RP --> DB
    ER --> DB
    SR --> DB
    HR --> DB

    style CLIENTE fill:#e1f5fe
    style API fill:#fff3e0
    style SERVICIO fill:#e8f5e9
    style MOTOR fill:#f3e5f5
    style INTEGRACION fill:#e0f2f1
    style REPOS fill:#fce4ec
    style DB fill:#ffebee
```

## Flujo de una simulación

```mermaid
sequenceDiagram
    participant C as Cliente
    participant CTL as SimulacionController
    participant SVC as SimulacionService
    participant FUENTE as FuenteSituacionAcademicaLocal
    participant REPO as EquivalenciaRepository
    participant MOTOR as MotorSimulacion
    participant MAP as SimulacionMapper
    participant DB as PostgreSQL

    C->>CTL: POST /api/v1/simulaciones<br/>X-Legajo + cuerpo JSON
    CTL->>SVC: simular(legajo, codigoPlanDestino)
    SVC->>FUENTE: obtenerEstudiante(legajo)
    SVC->>FUENTE: obtenerPlanActual(legajo)
    SVC->>REPO: buscarDetalle(codigoPlanDestino)
    SVC->>FUENTE: obtenerHistorial(legajo)
    SVC->>REPO: findVigentes(origen, destino, fecha)
    SVC->>MOTOR: simular(contexto)
    MOTOR->>MOTOR: aplicar reglas (misma materia, equivalencia)
    MOTOR-->>SVC: ResultadoSimulacion
    SVC->>MAP: toResponse(resultado, origen, destino)
    MAP-->>SVC: SimulacionResponse
    SVC-->>CTL: SimulacionResponse
    CTL-->>C: 200 OK + JSON
```

## Separación de responsabilidades

| Capa | Responsabilidad | No hace |
|---|---|---|
| **API** | Recibir HTTP, validar entrada, devolver JSON | Lógica de negocio, consultas SQL |
| **Servicio** | Casos de uso, transacciones, orquestación | Parsing HTTP, mapeo a JSON |
| **Motor** | Reglas de reconocimiento, algoritmo de simulación | Acceso a base de datos, HTTP |
| **Integración** | Adaptar la fuente académica (JPA local) | Lógica de negocio |
| **Repositorio** | Consultas a PostgreSQL | Lógica de negocio, transacciones |
| **DTO** | Contrato de salida/entrada | Lógica |

## Tecnologías

- **Java 21** — Lenguaje.
- **Spring Boot 4.1.2** — Framework principal.
- **Spring Web MVC** — Capa API REST.
- **Spring Data JPA / Hibernate** — Persistencia.
- **Flyway** — Migraciones de esquema y datos demo.
- **Spring Security** — Configuración (hoy permisiva, sin JWT).
- **Spring Boot Actuator** — Health check.
- **Lombok** — Reducción de boilerplate.
- **Maven Wrapper** — Build sin Maven instalado.
- **Testcontainers** — Tests de API con PostgreSQL efímero.
- **Docker / Docker Compose** — Despliegue todo en contenedores.
