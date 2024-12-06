package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz {@link BookService} que proporciona la lógica de negocio para la gestión de libros.
 * 
 * <p>Esta clase gestiona la creación, actualización, eliminación y búsqueda de libros, así como la integración con
 * el sistema de reportes para registrar acciones realizadas sobre los libros.</p>
 * 
 * @author Jose Murcia
 * @version 12/02/24
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReportEntryService reportEntryService;

    /**
     * Constructor que inyecta las dependencias necesarias para la gestión de libros.
     * 
     * @param bookRepository Repositorio para realizar operaciones CRUD sobre libros.
     * @param reportEntryService Servicio para registrar acciones en el sistema de reportes.
     */
    @Autowired 
    public BookServiceImpl(BookRepository bookRepository, ReportEntryService reportEntryService) {
        this.bookRepository = bookRepository;
        this.reportEntryService = reportEntryService;
    }

    /**
     * Recupera todos los libros registrados en el sistema.
     * 
     * @return Lista de libros existentes en el sistema.
     */
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Busca un libro por su identificador único.
     * 
     * @param id Identificador único del libro.
     * @return El libro correspondiente al identificador, o {@code null} si no se encuentra.
     */
    @Override
    public Book findBookById(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }

    /**
     * Guarda un libro en el sistema, ya sea nuevo o una actualización.
     * 
     * <p>Registra una acción en el sistema de reportes para indicar que se ha creado un nuevo libro.</p>
     * 
     * @param book Objeto {@link Book} que se desea guardar.
     * @return El libro guardado.
     */
    @Override
    @Transactional
    public Book saveBook(Book book) {
        Book savedBook = bookRepository.save(book);
        reportEntryService.createReport(
            ActionType.NEW_BOOK,
            "Se agregó el libro: " + savedBook.getTitle(),
            savedBook.getId()
        );
        return savedBook;
    }

    /**
     * Elimina un libro del sistema, si no tiene préstamos pendientes.
     * 
     * <p>Registra una acción en el sistema de reportes al eliminar un libro.</p>
     * 
     * @param id Identificador único del libro a eliminar.
     * @throws IllegalStateException Si el libro tiene préstamos pendientes.
     */
    @Override
    @Transactional
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            if (book.getLendings() != null && !book.getLendings().isEmpty()) {
                throw new IllegalStateException("El libro '" + book.getTitle() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            bookRepository.deleteById(id);
            reportEntryService.createReport(
                ActionType.DELETE_BOOK,
                "Se eliminó el libro: " + book.getTitle(),
                id
            );
        }
    }

    /**
     * Busca libros cuyos títulos contengan una cadena específica, ignorando mayúsculas y minúsculas.
     * 
     * @param title Cadena parcial o total a buscar en los títulos de los libros.
     * @return Lista de libros cuyos títulos contienen la cadena proporcionada.
     */
    @Override
    public List<Book> findBooksByTitleContaining(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    /**
     * Elimina múltiples libros seleccionados, siempre y cuando no tengan préstamos pendientes.
     * 
     * <p>Registra una acción en el sistema de reportes por cada libro eliminado.</p>
     * 
     * @param bookIds Lista de identificadores únicos de los libros a eliminar.
     * @throws IllegalArgumentException Si alguno de los libros no se encuentra.
     * @throws IllegalStateException Si alguno de los libros tiene préstamos pendientes.
     */
    @Override
    @Transactional
    public void bulkDeleteBooks(List<Integer> bookIds) {
        List<Book> booksToDelete = bookRepository.findAllById(bookIds);
        if (booksToDelete.size() != bookIds.size()) {
            throw new IllegalArgumentException("Algunos libros no fueron encontrados para eliminar.");
        }
        for (Book book : booksToDelete) {
            if (book.getLendings() != null && !book.getLendings().isEmpty()) {
                throw new IllegalStateException("El libro '" + book.getTitle() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            reportEntryService.createReport(
                ActionType.DELETE_BOOK,
                "Se eliminó el libro: " + book.getTitle(),
                book.getId()
            );
        }
        bookRepository.deleteAll(booksToDelete);
    }
}

