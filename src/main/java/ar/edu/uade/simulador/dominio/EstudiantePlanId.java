package ar.edu.uade.simulador.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;
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
public class EstudiantePlanId implements Serializable {

    @Column(name = "id_estudiante")
    private UUID idEstudiante;

    @Column(name = "id_plan")
    private Integer idPlan;
}
