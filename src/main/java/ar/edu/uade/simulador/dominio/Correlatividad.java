package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.TipoCorrelatividad;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "correlatividad")
@Getter
@Setter
@NoArgsConstructor
public class Correlatividad {

    @EmbeddedId
    private CorrelatividadId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plan", insertable = false, updatable = false)
    private PlanEstudio plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia", insertable = false, updatable = false)
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_materia_requerida", insertable = false, updatable = false)
    private Materia materiaRequerida;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoCorrelatividad tipo;
}
