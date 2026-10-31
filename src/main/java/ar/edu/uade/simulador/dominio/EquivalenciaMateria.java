package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.RolEquivalencia;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "equivalencia_materia")
@Getter
@Setter
@NoArgsConstructor
public class EquivalenciaMateria {

    @EmbeddedId
    private EquivalenciaMateriaId id;

    @MapsId("idEquivalencia")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_equivalencia", nullable = false)
    private Equivalencia equivalencia;

    @MapsId("idMateria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    public RolEquivalencia getRol() {
        return id == null ? null : id.getRol();
    }
}
