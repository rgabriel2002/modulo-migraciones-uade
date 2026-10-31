package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.RolEquivalencia;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equivalencia")
@Getter
@Setter
@NoArgsConstructor
public class Equivalencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equivalencia")
    private Integer idEquivalencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plan_origen", nullable = false)
    private PlanEstudio planOrigen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plan_destino", nullable = false)
    private PlanEstudio planDestino;

    @Column(name = "resolucion", nullable = false, length = 50)
    private String resolucion;

    @Column(name = "vigente_desde", nullable = false)
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @OneToMany(mappedBy = "equivalencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquivalenciaMateria> materias = new ArrayList<>();

    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(vigenteDesde) && (vigenteHasta == null || !fecha.isAfter(vigenteHasta));
    }

    public List<Materia> getMateriasOrigen() {
        return materias.stream()
                .filter(em -> em.getRol() == RolEquivalencia.ORIGEN)
                .map(EquivalenciaMateria::getMateria)
                .toList();
    }

    public Materia getMateriaDestino() {
        return materias.stream()
                .filter(em -> em.getRol() == RolEquivalencia.DESTINO)
                .map(EquivalenciaMateria::getMateria)
                .findFirst()
                .orElse(null);
    }
}
