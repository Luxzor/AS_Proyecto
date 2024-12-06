
package mycompany.LibrarySystem.controller;

import mycompany.LibrarySystem.model.entities.ReportEntry;
import mycompany.LibrarySystem.model.services.ReportEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *Controlador que gestiona los reportes almacenados en la base de datos del sistema.
 * 
 * <p>Permite eliminar reportes y listarlos segun un rango de fechas.</p>
 * <p>Pasa datos a la vista usando el objeto {@link Model}.</p>
 * 
 * @author David Escalante
 * @version 12/02/24
 */
@Controller
@RequestMapping("/reports")
public class ReportsController {

    private final ReportEntryService reportEntryService;

    /**
     *Constructor que inyecta el servicio de entrada de reportes.
     * 
     * @param reportEntryService servicio que proporciona acceso a las operaciones de reportes.
     */
    @Autowired
    public ReportsController(ReportEntryService reportEntryService) {
        this.reportEntryService = reportEntryService;
    }

    /**
     * Maneja solicitudes HTTP GET para listar reportes.
     *
     * <p>Lista los reportes según un rango de fechas determinado por fechas de inicio y fin, en caso de no proporcionarse 
     * fechas, devuelve todos los reportes disponibles.</p>
     * 
     * @param startDateStr fecha de inicio en formato de cadena (opcional).
     * @param endDateStr fecha de fin en formato de cadena (opcional).
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return
     */
    @GetMapping
    public String listReports(@RequestParam(value = "startDate", required = false) String startDateStr,
                              @RequestParam(value = "endDate", required = false) String endDateStr,
                              Model model) {
        List<ReportEntry> reports;

        if (startDateStr != null && endDateStr != null) {
            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            reports = reportEntryService.getReportsBetweenDates(startDateTime, endDateTime);
            model.addAttribute("startDate", startDateStr);
            model.addAttribute("endDate", endDateStr);
        } else {
            reports = reportEntryService.getAllReports();
        }

        model.addAttribute("reports", reports);
        return "reports/list";
    }

     /**
     *Maneja solicitudes HTTP GET para eliminar reportes.
     * 
     * <p>Elimina un reporte identificado segun su identificador. Si la operación tiene éxito, se 
     * añade un mensaje de confirmación, en caso de error, se notifica el problema.</p>
     * 
     * @param id identificador unico del reporte a eliminar
     * @param redirectAttributes objeto que pasa mensajes de notificacion segun el resultado de la operación.
     * @return Redirección a la lista de reportes {@code "redirect:/reports"}
     */
    @GetMapping("/delete/{id}")
    public String deleteReport(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            reportEntryService.deleteReport(id);
            redirectAttributes.addFlashAttribute("message", "Reporte eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el reporte: " + e.getMessage());
        }
        return "redirect:/reports";
    }
}
