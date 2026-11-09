package ar.edu.uade.simulador.seguridad;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ventana deslizante por clave (normalmente la IP) sin estado externo.
 * Sirve para una sola instancia; para múltiples nodos haría falta
 * Bucket4j + Redis.
 */
public class ControlTasa {

    /** A partir de esta cantidad de claves se dispara la limpieza perezosa. */
    private static final int UMBRAL_LIMPIEZA = 10_000;

    private final int maximoSolicitudes;
    private final long ventanaMs;
    private final Map<String, Deque<Instant>> solicitudes = new ConcurrentHashMap<>();

    public ControlTasa(int maximoSolicitudes, long ventanaMs) {
        this.maximoSolicitudes = maximoSolicitudes;
        this.ventanaMs = ventanaMs;
    }

    /** true si la petición pasa; false si la clave ya agotó su cuota. */
    public boolean permitir(String clave, Instant ahora) {
        Deque<Instant> recientes = solicitudes.computeIfAbsent(clave, k -> new ArrayDeque<>());
        synchronized (recientes) {
            Instant limite = ahora.minusMillis(ventanaMs);
            while (!recientes.isEmpty() && recientes.peekFirst().isBefore(limite)) {
                recientes.pollFirst();
            }
            if (recientes.size() >= maximoSolicitudes) {
                return false;
            }
            recientes.addLast(ahora);
            return true;
        }
    }

    /**
     * Limpieza perezosa: cuando el mapa crece demasiado, descarta las claves
     * que quedaron sin peticiones dentro de la ventana.
     */
    public void limpiar(Instant ahora) {
        if (solicitudes.size() < UMBRAL_LIMPIEZA) {
            return;
        }
        Instant limite = ahora.minusMillis(ventanaMs);
        solicitudes.entrySet().removeIf(entrada -> {
            Deque<Instant> cola = entrada.getValue();
            synchronized (cola) {
                while (!cola.isEmpty() && cola.peekFirst().isBefore(limite)) {
                    cola.pollFirst();
                }
                return cola.isEmpty();
            }
        });
    }
}
