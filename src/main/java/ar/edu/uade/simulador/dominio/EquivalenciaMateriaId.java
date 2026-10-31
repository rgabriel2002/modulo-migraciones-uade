package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.RolEquivalencia;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class EquivalenciaMateriaId implements Serializable {

    @Column(name = "id_equivalencia")
    private Integer idEquivalencia;

    @Column(name = "id_materia")
    private Integer idMateria;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", length = 10)
    private RolEquivalencia rol;
}
