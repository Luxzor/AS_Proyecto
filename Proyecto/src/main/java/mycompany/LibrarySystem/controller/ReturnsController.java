
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
 * Controlador que efectua las devoluciones pendientes de un usuario.
 * 
 * @author Jose Murcia
 * @version 12/02/24
 */
@Controller
@RequestMapping("/returns")
public class ReturnsController {

    private static final Logger logger = LoggerFactory.getLogger(ReturnsController.class);

    private final LendingService lendingService;

    /**
     * Constructor que inyecta el servicio de préstamos.
     * 
     * @param lendingService servicio que porporciona acceso a las operaciones de préstamos.
     */
    @Autowired
    public ReturnsController(LendingService lendingService) {
        this.lendingService = lendingService;
        logger.info("ReturnsController initialized");
    }

    
    /**
     * Redirige al formulario de devoluciones.
     * 
     * @return Redirección al formulario de devoluciones.
     */
    @GetMapping
    public String redirectToForm() {
        return "redirect:/returns/form";
    }

    
    /**
     * Muestra el formulario de devoluciones. 
     * 
     * @param model modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista del formulario de devoluciones.
     */
    @GetMapping("/form")
    public String showReturnForm(Model model) {
        return "returns/form";
    }

    
    /**
     * Busca los prestamos del usuario seleccionado
     * 
     * <p>En caso de no encontrar al usuario o prestamos asociados al usuario, desplegara un mensaje en la vista. En
     * caso contrario, mostrara las devoluciones asociadas al usuarios en la vista</p>
     * 
     * @param userName cadena con la que se buscara el nombre del usuario.
     * @param model modelo utilizado para pasar datos a la vista.
     * @return Nombre la vista del formulario de devoluciones.
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
     * Procesa y efectua la devolución en la base de datos.
     * 
     * @param lendingId identificador unico del préstamo.
     * @param userName cadena con la que se buscara el nombre del usuario.
     * @param model modelo utilizado para pasar datos a la vista.
     * @return
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

        
        if (userName != null && !userName.isEmpty()) {
            return "redirect:/returns/searchByUser?userName=" + userName;
        } else {
            return "redirect:/returns/form";
        }
    }
}
