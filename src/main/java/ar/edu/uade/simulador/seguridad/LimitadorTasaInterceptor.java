package ar.edu.uade.simulador.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Limita las peticiones por IP sobre /api/v1 para frenar la enumeración de
 * legajos y el abuso de la API. Responde 429 con el mismo cuerpo ErrorResponse
 * del resto del backend (escrito a mano para no depender del mapper).
 */
@Component
public class LimitadorTasaInterceptor implements HandlerInterceptor {

    private static final int MAXIMO_SOLICITUDES = 60;
    private static final long VENTANA_MS = 60_000;

    private final ControlTasa control = new ControlTasa(MAXIMO_SOLICITUDES, VENTANA_MS);

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // Detrás de un proxy se debería leer X-Forwarded-For en vez de la IP directa.
        String ip = request.getRemoteAddr();
        Instant ahora = Instant.now();

        control.limpiar(ahora);
        if (control.permitir(ip, ahora)) {
            return true;
        }

        response.setStatus(429);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"timestamp\":\"" + ahora
                + "\",\"status\":429,\"error\":\"Too Many Requests\","
                + "\"mensaje\":\"Demasiadas solicitudes. Intentá de nuevo en un minuto.\","
                + "\"ruta\":\"" + request.getRequestURI() + "\"}");
        return false;
    }
}
