package com.entidadfinanciera.app.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro de seguridad HTTP que intercepta todas las peticiones entrantes
 * e inyecta cabeceras de protección (Security Headers) en las respuestas HTTP.
 * <p>
 * Ayuda a mitigar vulnerabilidades comunes como Clickjacking, MIME-Sniffing
 * y almacenamiento no deseado de datos sensibles en la caché del cliente.
 */

@Component
public class SecurityHeadersConfig implements Filter {

    /**
     * Intercepta la petición/respuesta y agrega las cabeceras de seguridad requeridas.
     *
     * @param request  La solicitud HTTP entrante.
     * @param response La respuesta HTTP enviada al cliente.
     * @param chain    El flujo de ejecución de filtros de Servlet.
     * @throws IOException      Si ocurre un error de E/S durante la ejecución.
     * @throws ServletException Si ocurre un error en el procesamiento del servlet.
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        httpResponse.setHeader("X-Frame-Options", "DENY");
        httpResponse.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }
}