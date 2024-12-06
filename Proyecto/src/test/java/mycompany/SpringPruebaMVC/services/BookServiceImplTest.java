package mycompany.SpringPruebaMVC.services;

import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.BookRepository;
import mycompany.LibrarySystem.model.services.ReportEntryService;
import mycompany.LibrarySystem.model.services.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.List;
import mycompany.LibrarySystem.model.entities.Lending;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReportEntryService reportEntryService; // Mock para simular ReportEntryService

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializar mocks antes de cada prueba
    }

    @Test
    void testSaveBook() {
        // Arrange
        Book book = new Book(); // Crear un nuevo libro para guardar
        book.setTitle("Test Book");

        // Configurar el libro simulado que será retornado por el repositorio
        Book savedBook = new Book();
        savedBook.setId(1); // Establecer un ID válido para simular persistencia
        savedBook.setTitle("Test Book");

        // Configurar el comportamiento del repositorio
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        // Act
        Book result = bookService.saveBook(book);

        // Assert
        assertNotNull(result); // Verificar que el libro guardado no sea null
        assertEquals("Test Book", result.getTitle()); // Verificar el título del libro
        assertEquals(1, result.getId()); // Verificar que el ID del libro sea correcto

        // Verificar que el método createReport fue llamado con los argumentos esperados
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.NEW_BOOK), // Verificar el tipo de acción
                eq("Se agregó el libro: Test Book"), // Verificar el mensaje
                eq(1) // Verificar que el ID del libro sea el correcto
        );
    }

    @Test
    void testFindBookById_Found() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act
        Book result = bookService.findBookById(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Book", result.getTitle());

        // Verificar que el repositorio fue llamado con el ID correcto
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testFindBookById_NotFound() {
        // Arrange
        when(bookRepository.findById(1)).thenReturn(Optional.empty());

        // Act
        Book result = bookService.findBookById(1);

        // Assert
        assertNull(result);

        // Verificar que el repositorio fue llamado con el ID correcto
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteBook_Success() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        book.setLendings(null); // Sin préstamos pendientes

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act
        bookService.deleteBook(1);

        // Assert
        verify(bookRepository, times(1)).deleteById(1);
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.DELETE_BOOK),
                eq("Se eliminó el libro: Test Book"),
                eq(1)
        );
    }

    @Test
    void testDeleteBook_HasLendings() {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book"); // Título usado en el mensaje

        Lending lending = new Lending();
        book.setLendings(List.of(lending)); // Simular préstamos pendientes

        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookService.deleteBook(1);
        });

        assertEquals("El libro 'Test Book' tiene préstamos pendientes y no puede ser eliminado.", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyInt());
        verify(reportEntryService, never()).createReport(any(), any(), any());
    }

    @Test
    void testFindBooksByTitleContaining() {
        // Arrange
        List<Book> books = List.of(
                new Book() {
            {
                setId(1);
                setTitle("Java Basics");
            }
        },
                new Book() {
            {
                setId(2);
                setTitle("Advanced Java");
            }
        }
        );

        when(bookRepository.findByTitleContainingIgnoreCase("Java")).thenReturn(books);

        // Act
        List<Book> result = bookService.findBooksByTitleContaining("Java");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        assertEquals("Advanced Java", result.get(1).getTitle());

        // Verificar que el repositorio fue llamado con el término correcto
        verify(bookRepository, times(1)).findByTitleContainingIgnoreCase("Java");
    }

    @Test
    void testBulkDeleteBooks_Success() {
        // Arrange
        List<Integer> bookIds = List.of(1, 2);

        List<Book> books = List.of(
                new Book() {
            {
                setId(1);
                setTitle("Book 1");
                setLendings(null);
            }
        },
                new Book() {
            {
                setId(2);
                setTitle("Book 2");
                setLendings(null);
            }
        }
        );

        when(bookRepository.findAllById(bookIds)).thenReturn(books);

        // Act
        bookService.bulkDeleteBooks(bookIds);

        // Assert
        verify(bookRepository, times(1)).deleteAll(books);
        verify(reportEntryService, times(2)).createReport(any(), any(), any());
    }

    @Test
    void testBulkDeleteBooks_HasLendings() {
        // Arrange
        List<Integer> bookIds = List.of(1, 2);

        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Book 1");
        book1.setLendings(null); // Sin préstamos para este libro

        Book book2 = new Book();
        book2.setId(2);
        book2.setTitle("Book 2");

        // Crear un préstamo simulado para el libro 2
        Lending lending = new Lending();
        book2.setLendings(List.of(lending)); // Con préstamos pendientes

        // Simular que estos libros son encontrados en la base de datos
        when(bookRepository.findAllById(bookIds)).thenReturn(List.of(book1, book2));

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookService.bulkDeleteBooks(bookIds);
        });

        assertEquals("El libro 'Book 2' tiene préstamos pendientes y no puede ser eliminado.", exception.getMessage());

        // Verificar que el reporte se creó solo para el libro sin préstamos
        verify(reportEntryService, times(1)).createReport(
                eq(ActionType.DELETE_BOOK),
                eq("Se eliminó el libro: Book 1"),
                eq(1)
        );

        // Verificar que el repositorio no intentó eliminar libros en bloque
        verify(bookRepository, never()).deleteAll(any());
    }

}
