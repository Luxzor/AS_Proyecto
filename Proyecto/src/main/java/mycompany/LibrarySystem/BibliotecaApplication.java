package mycompany.LibrarySystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;


/**
 * Clase principal de la aplicación para la gestión de la biblioteca
 * 
 * <p>Configura y lanza la aplicación SpringBoot. Contiene el punto de entrada del programa y una configuración adicional
 * para personalizar el servidor web embebido</p>
 * <ul>
 * <li>La anotación {@code @SpringBootApplication} marca esta clase como la clase principal y habilita características 
 * clave de Spring Boot.</li>
 * <li>El método {@code main} utiliza {@link SpringApplication#run} para iniciar 
 * la aplicación.</li>
 * <li>El bean (componente que mantiene el flujo de dependencias) {@code webServerFactoryCustomizer} personaliza el servidor 
 * web embebido configurando la codificación de caracteres como UTF-8.</li>
 * </ul>
 * 
 * @author Luis Montero
 * @version 30/11/2024
 */
@SpringBootApplication
public class BibliotecaApplication {


    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }    
    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> factory.addInitializers(servletContext -> {
            servletContext.setInitParameter("characterEncoding", "UTF-8");
        });
    }
}

