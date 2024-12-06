package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.ReportEntry;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.ReportEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que implementa los métodos definidos en la interfaz {@link ReportEntryService} para la gestión de reportes.
 * 
 * <p>Esta clase actúa como la capa de servicio para manejar las operaciones relacionadas con los reportes del sistema, 
 * como la creación, búsqueda y eliminación de reportes.</p>
 * 
 * 
 * @author Sebastian Laines
 * @version 02/12/2024
 */
@Service
public class ReportEntryServiceImpl implements ReportEntryService {

    private final ReportEntryRepository reportEntryRepository;

    /**
     * Constructor que inyecta la dependencia {@link ReportEntryRepository}.
     * 
     * @param reportEntryRepository Repositorio que proporciona acceso a las operaciones relacionadas con los reportes.
     */
    @Autowired
    public ReportEntryServiceImpl(ReportEntryRepository reportEntryRepository) {
        this.reportEntryRepository = reportEntryRepository;
    }

    /**
     * Crea un nuevo reporte en el sistema.
     * 
     * <p>El reporte incluye el tipo de acción, una descripción, la entidad asociada y la fecha de creación.</p>
     * 
     * @param actionType Tipo de acción que se está reportando. Ejemplo: {@code ActionType.NEW_USER}.
     * @param description Descripción detallada del reporte.
     * @param relatedEntityId Identificador único de la entidad asociada al reporte (por ejemplo, un usuario o libro).
     */
    @Override
    public void createReport(ActionType actionType, String description, Integer relatedEntityId) {
        ReportEntry report = new ReportEntry();
        report.setActionType(actionType);
        report.setDescription(description);
        report.setTimestamp(LocalDateTime.now());
        report.setRelatedEntityId(relatedEntityId);
        reportEntryRepository.save(report);
    }

    /**
     * Devuelve todos los reportes registrados en el sistema.
     * 
     * @return Lista de todos los objetos {@link ReportEntry} almacenados en la base de datos.
     */
    @Override
    public List<ReportEntry> getAllReports() {
        return reportEntryRepository.findAll();
    }

    /**
     * Devuelve los reportes cuya fecha de creación está dentro de un rango especificado.
     * 
     * @param start Fecha inicial del rango.
     * @param end Fecha final del rango.
     * @return Lista de reportes que cumplen con el rango de fechas especificado.
     */
    @Override
    public List<ReportEntry> getReportsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return reportEntryRepository.findByTimestampBetween(start, end);
    }

    /**
     * Elimina un reporte del sistema basado en su identificador único.
     * 
     * @param id Identificador único del reporte a eliminar.
     */
    @Override
    public void deleteReport(Integer id) {
        reportEntryRepository.deleteById(id);
    }
}

