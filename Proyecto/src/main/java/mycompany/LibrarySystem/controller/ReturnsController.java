package mycompany.LibrarySystem.controller;

import mycompany.LibrarySystem.model.entities.Lending;
import mycompany.LibrarySystem.model.services.LendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controlador que gestiona las devoluciones de préstamos en el sistema.
 * 
 * <p>Proporciona funcionalidades para buscar préstamos pendientes por usuario y 
 * procesar devoluciones.</p>
 * 
 * <p>Los datos se envían a las vistas utilizando el objeto {@link Model}.</p>
 * 
 * @author José Murcia
 * @version 12/02/2024
 */
@Controller
@RequestMapping("/returns")
public class ReturnsController {

    private static final Logger logger = LoggerFactory.getLogger(ReturnsController.class);

    private final LendingService lendingService;

    /**
     * Constructor que inyecta el servicio de préstamos.
     * 
     * @param lendingService Servicio que proporciona acceso a las operaciones relacionadas con los préstamos.
     */
    @Autowired
    public ReturnsController(LendingService lendingService) {
        this.lendingService = lendingService;
        logger.info("ReturnsController initialized");
    }

    /**
     * Redirige al formulario principal de devoluciones.
     * 
     * @return Redirección a la vista del formulario de devoluciones.
     */
    @GetMapping
    public String redirectToForm() {
        return "redirect:/returns/form";
    }

    /**
     * Muestra el formulario de devoluciones.
     * 
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista del formulario de devoluciones.
     */
    @GetMapping("/form")
    public String showReturnForm(Model model) {
        return "returns/form";
    }

    /**
     * Busca préstamos pendientes asociados a un usuario específico.
     * 
     * <p>Si no se encuentran préstamos pendientes para el usuario proporcionado, se muestra
     * un mensaje en la vista. En caso contrario, se listan los préstamos pendientes.</p>
     * 
     * @param userName Nombre del usuario para realizar la búsqueda.
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista del formulario de devoluciones.
     */
    @GetMapping("/searchByUser")
    public String searchByUser(@RequestParam("userName") String userName, Model model) {
        logger.info("Buscando préstamos pendientes para el usuario: {}", userName);
        List<Lending> lendings = lendingService.searchPendingLendingsByUser(userName);
        if (lendings.isEmpty()) {
            model.addAttribute("message", "No se encontraron préstamos pendientes para el usuario: " + userName);
            logger.info("No se encontraron préstamos pendientes para el usuario: {}", userName);
        } else {
            model.addAttribute("lendings", lendings);
            model.addAttribute("userName", userName);
            logger.info("Encontrados {} préstamos pendientes para el usuario: {}", lendings.size(), userName);
        }
        return "returns/form";
    }

    /**
     * Procesa la devolución de un préstamo específico.
     * 
     * <p>Si la devolución es exitosa, se notifica al usuario mediante un mensaje. Si ocurre
     * un error, también se muestra un mensaje en la vista. Tras procesar la devolución, se redirige 
     * al formulario de búsqueda o al formulario principal de devoluciones.</p>
     * 
     * @param lendingId Identificador único del préstamo a devolver.
     * @param userName Nombre del usuario asociado al préstamo (opcional, para mantener la búsqueda activa).
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Redirección al formulario de búsqueda con el nombre del usuario (si se proporcionó),
     *         o al formulario principal de devoluciones.
     */
    @PostMapping("/process")
    public String processReturn(@RequestParam("lendingId") Integer lendingId,
                                @RequestParam(value = "userName", required = false) String userName,
                                Model model) {
        logger.info("Procesando devolución para lendingId: {}", lendingId);
        try {
            lendingService.processReturn(lendingId);
            model.addAttribute("message", "Devolución realizada exitosamente.");
            logger.info("Devolución exitosa para lendingId: {}", lendingId);
        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar la devolución: " + e.getMessage());
            logger.error("Error al procesar la devolución para lendingId {}: {}", lendingId, e.getMessage());
        }

        // Retorna al formulario de búsqueda si el nombre del usuario está presente, de lo contrario al formulario principal
        if (userName != null && !userName.isEmpty()) {
            return "redirect:/returns/searchByUser?userName=" + userName;
        } else {
            return "redirect:/returns/form";
        }
    }
}

