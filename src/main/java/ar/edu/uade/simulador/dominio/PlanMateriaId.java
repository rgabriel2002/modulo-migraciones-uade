package ar.edu.uade.simulador.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlanMateriaId implements Serializable {

    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "id_materia")
    private Integer idMateria;
}
