// src/main/java/mycompany/SpringPruebaMVC/model/services/ReportEntryService.java

package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.ReportEntry;
import mycompany.LibrarySystem.model.entities.ActionType;
import java.time.LocalDateTime;
import java.util.List;

/**
 *Interfaz que define los servicios relacionados con la gestión de reportes del sistema.
 * 
 * <p>Actua como un contrato para la capa de servicios, proporcionando un metodo para encontrar todos los reportes pendientes
 * por nombre de usuario</p>
 * 
 * @author Sebastian Laines
 * @version 02/12/2024
 */
public interface ReportEntryService {

    /**
     *Crea un nuevo reporte en el sistema.
     * 
     * @param actionType el tipo de acción que se esta reportando.
     * @param description descripción del reporte.
     * @param relatedEntityId identificador unico de la entidad asociada al reporte.
     */
    void createReport(ActionType actionType, String description, Integer relatedEntityId);

    /**
     *Devuelve todos los reportes del sistema.
     * 
     * @return lista de todos los reportes del sistema.
     */
    List<ReportEntry> getAllReports();

    /**
     *Devuelve reportes en un rango de fechas especificado.
     * 
     * @param start fecha inicial del rango.
     * @param end fecha final del rango.
     * @return Lista de reportes cuya fecha se encuentre dentro del rango proporcionado.
     */
    List<ReportEntry> getReportsBetweenDates(LocalDateTime start, LocalDateTime end);

    /**
     *Elimina un reporte segun su id.
     * 
     * @param id identificador unico del reporte a eliminar.
     */
    void deleteReport(Integer id);
}
