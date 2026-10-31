package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.EstadoInscripcion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estudiante")
@Getter
@Setter
@NoArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_estudiante")
    private UUID idEstudiante;

    @Column(name = "legajo", nullable = false, unique = true, length = 50)
    private String legajo;

    @Column(name = "dni", unique = true, length = 15)
    private String dni;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstudiantePlan> inscripciones = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialAcademico> historial = new ArrayList<>();

    public EstudiantePlan getPlanActivo() {
        return inscripciones.stream()
                .filter(ep -> ep.getEstado() == EstadoInscripcion.ACTIVO)
                .findFirst()
                .orElse(null);
    }
}
