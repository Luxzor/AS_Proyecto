package mycompany.SpringPruebaMVC.services;

import mycompany.LibrarySystem.model.entities.Lending;
import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.repositories.LendingRepository;
import mycompany.LibrarySystem.model.services.BookService;
import mycompany.LibrarySystem.model.services.LendingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class LendingServiceImplTest {

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private BookService bookService;

    @InjectMocks
    private LendingServiceImpl lendingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllLendings() {
        // Arrange
        List<Lending> lendings = List.of(new Lending(), new Lending());
        when(lendingRepository.findAll()).thenReturn(lendings);

        // Act
        List<Lending> result = lendingService.findAllLendings();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lendingRepository, times(1)).findAll();
    }

    @Test
    void testFindLendingById_Found() {
        // Arrange
        Lending lending = new Lending();
        lending.setId(1);
        when(lendingRepository.findById(1)).thenReturn(Optional.of(lending));

        // Act
        Lending result = lendingService.findLendingById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(lendingRepository, times(1)).findById(1);
    }

    @Test
    void testFindLendingById_NotFound() {
        // Arrange
        when(lendingRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        Lending result = lendingService.findLendingById(1);

        // Assert
        assertNull(result);
        verify(lendingRepository, times(1)).findById(1);
    }

    @Test
    void testSaveLending() {
        // Arrange
        Lending lending = new Lending();
        when(lendingRepository.save(lending)).thenReturn(lending);

        // Act
        Lending result = lendingService.saveLending(lending);

        // Assert
        assertNotNull(result);
        verify(lendingRepository, times(1)).save(lending);
    }

    @Test
    void testDeleteLending() {
        // Arrange
        Integer lendingId = 1;

        // Act
        lendingService.deleteLending(lendingId);

        // Assert
        verify(lendingRepository, times(1)).deleteById(lendingId);
    }

    @Test
    void testSearchPendingLendingsByUser() {
        // Arrange
        List<Lending> pendingLendings = List.of(new Lending(), new Lending());
        when(lendingRepository.searchPendingLendingsByUser("user")).thenReturn(pendingLendings);

        // Act
        List<Lending> result = lendingService.searchPendingLendingsByUser("user");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lendingRepository, times(1)).searchPendingLendingsByUser("user");
    }

    @Test
    void testProcessReturn_Success() {
        // Arrange
        Lending lending = new Lending();
        lending.setId(1);
        lending.setDateReturn(null);

        Book book = new Book();
        book.setAvailable(5);
        lending.setBook(book);

        when(lendingRepository.findById(1)).thenReturn(Optional.of(lending));

        // Act
        lendingService.processReturn(1);

        // Assert
        assertEquals(LocalDate.now(), lending.getDateReturn());
        assertEquals(6, book.getAvailable()); // Verificar que el stock aumentó
        verify(lendingRepository, times(1)).save(lending);
        verify(bookService, times(1)).saveBook(book);
    }

    @Test
    void testProcessReturn_AlreadyReturned() {
        // Arrange
        Lending lending = new Lending();
        lending.setId(1);
        lending.setDateReturn(LocalDate.now());

        when(lendingRepository.findById(1)).thenReturn(Optional.of(lending));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            lendingService.processReturn(1);
        });

        assertEquals("El préstamo ya ha sido devuelto.", exception.getMessage());
        verify(lendingRepository, never()).save(any());
        verify(bookService, never()).saveBook(any());
    }

    @Test
    void testProcessReturn_NotFound() {
        // Arrange
        when(lendingRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            lendingService.processReturn(1);
        });

        assertEquals("Préstamo no encontrado.", exception.getMessage());
        verify(lendingRepository, never()).save(any());
        verify(bookService, never()).saveBook(any());
    }

}
