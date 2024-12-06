package mycompany.SpringPruebaMVC.services;

import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.entities.ReportEntry;
import mycompany.LibrarySystem.model.repositories.ReportEntryRepository;
import mycompany.LibrarySystem.model.services.ReportEntryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReportEntryServiceImplTest {

    @Mock
    private ReportEntryRepository reportEntryRepository;

    @InjectMocks
    private ReportEntryServiceImpl reportEntryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReport() {
        // Arrange
        ActionType actionType = ActionType.NEW_BOOK;
        String description = "A new book was added.";
        Integer relatedEntityId = 1;

        // Act
        reportEntryService.createReport(actionType, description, relatedEntityId);

        // Assert
        verify(reportEntryRepository, times(1)).save(argThat(report -> 
            report.getActionType() == actionType &&
            report.getDescription().equals(description) &&
            report.getRelatedEntityId().equals(relatedEntityId) &&
            report.getTimestamp() != null // Confirm that timestamp is set
        ));
    }

    @Test
    void testGetAllReports() {
        // Arrange
        List<ReportEntry> mockReports = List.of(
                new ReportEntry() {{ setId(1); setDescription("Report 1"); }},
                new ReportEntry() {{ setId(2); setDescription("Report 2"); }}
        );
        when(reportEntryRepository.findAll()).thenReturn(mockReports);

        // Act
        List<ReportEntry> result = reportEntryService.getAllReports();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reportEntryRepository, times(1)).findAll();
    }

    @Test
    void testGetReportsBetweenDates() {
        // Arrange
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<ReportEntry> mockReports = List.of(
                new ReportEntry() {{ setId(1); setTimestamp(start.plusDays(1)); }},
                new ReportEntry() {{ setId(2); setTimestamp(start.plusDays(2)); }}
        );
        when(reportEntryRepository.findByTimestampBetween(start, end)).thenReturn(mockReports);

        // Act
        List<ReportEntry> result = reportEntryService.getReportsBetweenDates(start, end);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reportEntryRepository, times(1)).findByTimestampBetween(start, end);
    }

    @Test
    void testDeleteReport() {
        // Arrange
        Integer reportId = 1;

        // Act
        reportEntryService.deleteReport(reportId);

        // Assert
        verify(reportEntryRepository, times(1)).deleteById(reportId);
    }
}
