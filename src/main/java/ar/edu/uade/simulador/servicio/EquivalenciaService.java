package ar.edu.uade.simulador.servicio;

import ar.edu.uade.simulador.dominio.Equivalencia;
import ar.edu.uade.simulador.dominio.Materia;
import ar.edu.uade.simulador.dto.EquivalenciaDTO;
import ar.edu.uade.simulador.dto.MateriaRefDTO;
import ar.edu.uade.simulador.repositorio.EquivalenciaRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EquivalenciaService {

    private final EquivalenciaRepository equivalenciaRepository;

    public List<EquivalenciaDTO> listar(String planOrigen, String planDestino) {
        LocalDate hoy = LocalDate.now();
        return equivalenciaRepository.buscarTodas().stream()
                .filter(e -> planOrigen == null || e.getPlanOrigen().getCodigoPlan().equals(planOrigen))
                .filter(e -> planDestino == null || e.getPlanDestino().getCodigoPlan().equals(planDestino))
                .map(e -> toDto(e, hoy))
                .toList();
    }

    private EquivalenciaDTO toDto(Equivalencia equivalencia, LocalDate hoy) {
        List<MateriaRefDTO> materiasOrigen = equivalencia.getMateriasOrigen().stream()
                .map(this::toRef)
                .toList();
        Materia destino = equivalencia.getMateriaDestino();

        return new EquivalenciaDTO(
                equivalencia.getResolucion(),
                equivalencia.getPlanOrigen().getCodigoPlan(),
                equivalencia.getPlanDestino().getCodigoPlan(),
                materiasOrigen,
                destino == null ? null : toRef(destino),
                new EquivalenciaDTO.VigenciaDTO(
                        equivalencia.getVigenteDesde(), equivalencia.getVigenteHasta()),
                equivalencia.estaVigente(hoy));
    }

    private MateriaRefDTO toRef(Materia materia) {
        return new MateriaRefDTO(materia.getCodigo(), materia.getNombre());
    }
}
