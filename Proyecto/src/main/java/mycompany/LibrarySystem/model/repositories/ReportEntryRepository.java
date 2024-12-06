// src/main/java/mycompany/SpringPruebaMVC/model/repositories/ReportEntryRepository.java

package mycompany.LibrarySystem.model.repositories;

import mycompany.LibrarySystem.model.entities.ReportEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz que proporciona consulta en base a rango de tiempo.
 * 
 * @author José Murcia
 * @version 02/12/24
 */
@Repository
public interface ReportEntryRepository extends JpaRepository<ReportEntry, Integer> {
    List<ReportEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
