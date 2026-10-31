package ar.edu.uade.simulador.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.util.Comparator;
import java.util.Optional;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plan_materia")
@Getter
@Setter
@NoArgsConstructor
public class PlanMateria {

    @EmbeddedId
    private PlanMateriaId id;

    @MapsId("idPlan")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plan", nullable = false)
    private PlanEstudio plan;

    @MapsId("idMateria")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @Column(name = "anio_plan", nullable = false)
    private Short anioPlan;

    @Column(name = "cuatrimestre")
    private Short cuatrimestre;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Column(name = "es_optativa", nullable = false)
    private boolean esOptativa;

    @Column(name = "es_titulo_intermedio", nullable = false)
    private boolean esTituloIntermedio;

    public static Comparator<PlanMateria> porUbicacion() {
        return Comparator.comparing(PlanMateria::getAnioPlan)
                .thenComparing(pm -> Optional.ofNullable(pm.getCuatrimestre()).orElse((short) 0))
                .thenComparing(pm -> pm.getMateria().getCodigo());
    }
}
