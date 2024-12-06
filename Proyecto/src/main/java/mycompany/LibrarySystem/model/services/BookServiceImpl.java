

package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ReportEntryService reportEntryService;

    
    /**
     *Constructor que inyecta las dependencias {@link BookRepository} y {@link ReportEntryService}.
     * 
     * @param bookRepository interfaz que permite el uso de la busqueda personalizada de libros.
     * @param reportEntryService interfaz que permite implementacion de operaciones con reportes.
     */
    @Autowired 
    public BookServiceImpl(BookRepository bookRepository, ReportEntryService reportEntryService) {
        this.bookRepository = bookRepository;
        this.reportEntryService = reportEntryService;
    }

    /**
     *Devuelve todos los libros del sistema.
     * 
     * @return Lista de libros registrados en el sistema.
     */
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    /**
     *Busca y devuelve un libro segun su identificador.
     * 
     * @param id identificador unico del libro.
     * @return Libro que corresponda al identificador, en caso de no encontrarse, devolverá {@code null}.
     */
    @Override
    public Book findBookById(Integer id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        return optionalBook.orElse(null);
    }
    
    /**
     *Guarda un libro en el sistema.
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param book libro que se guardara o actualizará.
     * @return Libro guardado.
     */
    @Override
    @Transactional
    public Book saveBook(Book book) {
        Book savedBook = bookRepository.save(book);
        // Crear reporte de nuevo libro
        reportEntryService.createReport(
            ActionType.NEW_BOOK,
            "Se agregó el libro: " + savedBook.getTitle(),
            savedBook.getId()
        );
        return savedBook;
    }
    
    /**
     *Elimina un libro del sistema.
     * 
     * <p>En caso de que el libro tenga un prestamo pendiente, lo notificará y arrojará una excepción. En caso
     * contrario eliminará el libro y creará un reporte de la acción.</p>
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param id identificador unico del libro a eliminar.
     */
    @Override
    @Transactional
    public void deleteBook(Integer id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            // Verificar si el libro tiene préstamos pendientes
            if (book.getLendings() != null && !book.getLendings().isEmpty()) {
                throw new IllegalStateException("El libro '" + book.getTitle() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            bookRepository.deleteById(id);
            // Crear reporte de eliminación de libro
            reportEntryService.createReport(
                ActionType.DELETE_BOOK,
                "Se eliminó el libro: " + book.getTitle(),
                id
            );
        }
    }

    /**
     *Encuentra a los libros que contengan una cadena proporcionada en el titulo.
     * 
     * <p>Ignora las mayusculas y minusculas.</p>
     * 
     * @param title cadena a buscar en los titulos de los libros.
     * @return Lista de libros cuyos titulos contengan la cadena proporcionada.
     */
    @Override
    public List<Book> findBooksByTitleContaining(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     *Elimina varios libros seleccionados.
     * 
     * <p>En caso de que no se encuentren todos los libros a eliminar, arrojará una excepción. En caso contrario intentará
     * eliminar los libros a menos que se encuentre uno con un prestamo pendiente, lo que también arrojaría una excepción.</p>
     * 
     * <p>Si ninguno de los libros tiene prestamos, se eliminan todos los libros y se crea un reporte de la acción.</p>
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param bookIds lista de indetificadores unicos de los libros a eliminar.
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
            // Crear reporte de eliminación de libro
            reportEntryService.createReport(
                ActionType.DELETE_BOOK,
                "Se eliminó el libro: " + book.getTitle(),
                book.getId()
            );
        }
        bookRepository.deleteAll(booksToDelete);
    }
}
