package ar.edu.uade.simulador.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ControlTasaTest {

    private static final Instant BASE = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void permiteHastaElLimiteYBloqueaElExcedente() {
        ControlTasa control = new ControlTasa(3, 60_000);

        assertTrue(control.permitir("ip-1", BASE));
        assertTrue(control.permitir("ip-1", BASE.plusSeconds(1)));
        assertTrue(control.permitir("ip-1", BASE.plusSeconds(2)));
        assertFalse(control.permitir("ip-1", BASE.plusSeconds(3)));
    }

    @Test
    void laVentanaSeDeslizaYLiberaCupo() {
        ControlTasa control = new ControlTasa(2, 60_000);

        assertTrue(control.permitir("ip-1", BASE));
        assertTrue(control.permitir("ip-1", BASE.plusSeconds(1)));
        assertFalse(control.permitir("ip-1", BASE.plusSeconds(2)));
        // Las dos primeras cayeron fuera de la ventana de 60 s.
        assertTrue(control.permitir("ip-1", BASE.plusSeconds(62)));
    }

    @Test
    void lasClavesSonIndependientes() {
        ControlTasa control = new ControlTasa(1, 60_000);

        assertTrue(control.permitir("ip-1", BASE));
        assertFalse(control.permitir("ip-1", BASE.plusSeconds(1)));
        assertTrue(control.permitir("ip-2", BASE.plusSeconds(1)));
    }

    @Test
    void laLimpiezaDescartaClavesViejas() {
        ControlTasa control = new ControlTasa(10, 60_000);
        for (int i = 0; i < 10_100; i++) {
            control.permitir("ip-" + i, BASE);
        }

        control.limpiar(BASE.plusSeconds(120));

        // Todas las claves quedaron fuera de la ventana: el mapa debería
        // haberse vaciado y volver a aceptar peticiones.
        assertTrue(control.permitir("ip-1", BASE.plusSeconds(121)));
    }
}
