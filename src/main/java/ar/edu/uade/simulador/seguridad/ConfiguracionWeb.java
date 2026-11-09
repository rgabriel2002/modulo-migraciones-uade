package ar.edu.uade.simulador.seguridad;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class ConfiguracionWeb implements WebMvcConfigurer {

    private final LimitadorTasaInterceptor limitadorTasa;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(limitadorTasa).addPathPatterns("/api/v1/**");
    }
}
