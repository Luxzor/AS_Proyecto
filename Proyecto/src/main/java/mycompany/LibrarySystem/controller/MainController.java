package mycompany.LibrarySystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


/**
 *Controlador principal que gestiona las rutas iniciales del sistema.
 * 
 * <p>Esta clase define los puntos principales de entrada para la navegación de la aplicación web.
 * Aunque solo esta habilitado la ruta raíz.</p>
 * 
 * @author David Escalante
 * @version 02/12/2024
 */
@Controller
public class MainController {

    /**
     *Maneja solicitudes HTTP GET para la página principal.
     * 
     * <p>Redirige al usuario a la pagina principal</p>
     * @return Nombre de la vista principal
     */
    @GetMapping("/")
    public String principal() {
        return "principal";
    }
}
