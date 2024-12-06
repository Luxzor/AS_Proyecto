// src/main/java/mycompany/SpringPruebaMVC/model/services/ReportEntryServiceImpl.java

package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.ReportEntry;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.ReportEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 *Clase que implementa los metodos de gestion de libros definidos en la interfaz ReportEntryService.
 * 
 * @author Sebastian Laines
 * @version 02/12/24
 */ 
@Service
public class ReportEntryServiceImpl implements ReportEntryService {

    private final ReportEntryRepository reportEntryRepository;

    /**
     *Constructor que inyecta la dependencia {@link ReportEntryService}.
     * 
     * @param reportEntryRepository interfaz que permite el uso de la operación de busqueda por rango de tiempo.
     */
    @Autowired
    public ReportEntryServiceImpl(ReportEntryRepository reportEntryRepository) {
        this.reportEntryRepository = reportEntryRepository;
    }

    /**
     *Crea un nuevo reporte en el sistema.
     * 
     * <p>Ademas de los parametros del metodo, tambien guarda la fecha de creación del reporte</p>
     * 
     * @param actionType tipo de acción que se esta reportando.
     * @param description descripción del reporte.
     * @param relatedEntityId identificador unico de la entidad asociada al reporte.
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
     *Devuelve todos los reportes del sistema.
     * 
     * @return lista de todos los reportes guardados en el sistema.
     */
    @Override
    public List<ReportEntry> getAllReports() {
        return reportEntryRepository.findAll();
    }

    /**
     *Devuelve los reportes cuya fecha coincidan con un rango proporcionado.
     * 
     * @param start fecha inicial del rango.
     * @param end fecha final del rango.
     * @return Lista de reportes cuya fecha se encuentre dentro del rango proporcionado.
     */
    @Override
    public List<ReportEntry> getReportsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return reportEntryRepository.findByTimestampBetween(start, end);
    }

    /**
     *Elimina un reporte del sistema. 
     * 
     * @param id identificador unico del reporte a eliminar.
     */
    @Override
    public void deleteReport(Integer id) {
        reportEntryRepository.deleteById(id);
    }
}
