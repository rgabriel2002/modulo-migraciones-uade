package ar.edu.uade.simulador.dominio;

import ar.edu.uade.simulador.dominio.enums.CondicionCursada;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "detalle_cursada")
@Getter
@Setter
@NoArgsConstructor
public class DetalleCursada {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_detalle")
    private UUID idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_historial", nullable = false)
    private HistorialAcademico historial;

    @Column(name = "anio_cursada", nullable = false)
    private Short anioCursada;

    @Column(name = "cuatrimestre")
    private Short cuatrimestre;

    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_alcanzada", length = 20)
    private CondicionCursada condicionAlcanzada;

    @Column(name = "nota_cierre_cursada", precision = 4, scale = 2)
    private BigDecimal notaCierreCursada;
}
