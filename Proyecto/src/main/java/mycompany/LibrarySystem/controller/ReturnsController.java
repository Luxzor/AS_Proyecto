
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

@Controller
@RequestMapping("/returns")
public class ReturnsController {

    private static final Logger logger = LoggerFactory.getLogger(ReturnsController.class);

    private final LendingService lendingService;

    @Autowired
    public ReturnsController(LendingService lendingService) {
        this.lendingService = lendingService;
        logger.info("ReturnsController initialized");
    }

    
    @GetMapping
    public String redirectToForm() {
        return "redirect:/returns/form";
    }

    
    @GetMapping("/form")
    public String showReturnForm(Model model) {
        return "returns/form";
    }

    
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
