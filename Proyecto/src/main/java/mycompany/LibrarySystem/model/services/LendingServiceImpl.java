package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.Lending;
import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.repositories.LendingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Clase que implementa los métodos definidos en la interfaz {@link LendingService} para la gestión de préstamos.
 * 
 * <p>Proporciona la lógica de negocio necesaria para realizar operaciones sobre los préstamos, como la creación,
 * actualización, eliminación y búsqueda de préstamos pendientes de devolución.</p>
 * 
 * @author Luis Montero
 * @version 02/12/2024
 */
@Service
public class LendingServiceImpl implements LendingService {

    private final LendingRepository lendingRepository;
    private final BookService bookService;

    /**
     * Constructor que inyecta las dependencias {@link LendingRepository} y {@link BookService}.
     * 
     * @param lendingRepository Repositorio que proporciona acceso a las operaciones relacionadas con préstamos.
     * @param bookService Servicio que permite gestionar las operaciones relacionadas con libros.
     */
    @Autowired
    public LendingServiceImpl(LendingRepository lendingRepository, BookService bookService) {
        this.lendingRepository = lendingRepository;
        this.bookService = bookService;
    }

    /**
     * Devuelve todos los préstamos registrados en el sistema.
     * 
     * @return Lista de objetos {@link Lending} que representan los préstamos existentes en la base de datos.
     */
    @Override
    public List<Lending> findAllLendings() {
        return lendingRepository.findAll();
    }

    /**
     * Busca y devuelve un préstamo basado en su identificador único.
     * 
     * @param id Identificador único del préstamo.
     * @return Objeto {@link Lending} que corresponde al identificador proporcionado, o {@code null} si no se encuentra.
     */
    @Override
    public Lending findLendingById(Integer id) {
        Optional<Lending> optionalLending = lendingRepository.findById(id);
        return optionalLending.orElse(null);
    }

    /**
     * Guarda o actualiza un préstamo en el sistema.
     * 
     * <p>Este método es transaccional, lo que significa que todas las operaciones dentro de este método se 
     * confirmarán o revertirán como una sola unidad.</p>
     * 
     * @param lending Objeto {@link Lending} que se desea guardar o actualizar.
     * @return El objeto {@link Lending} guardado o actualizado.
     */
    @Override
    @Transactional
    public Lending saveLending(Lending lending) {
        return lendingRepository.save(lending);
    }

    /**
     * Elimina un préstamo del sistema basado en su identificador único.
     * 
     * @param id Identificador único del préstamo a eliminar.
     */
    @Override
    public void deleteLending(Integer id) {
        lendingRepository.deleteById(id);
    }

    /**
     * Busca préstamos pendientes de un usuario basándose en un término de búsqueda.
     * 
     * <p>El término de búsqueda puede coincidir con el nombre, apellido paterno o apellido materno del usuario asociado.</p>
     * 
     * @param searchTerm Término de búsqueda utilizado para encontrar los préstamos pendientes del usuario.
     * @return Lista de objetos {@link Lending} que coinciden con el término de búsqueda.
     */
    @Override
    public List<Lending> searchPendingLendingsByUser(String searchTerm) {
        return lendingRepository.searchPendingLendingsByUser(searchTerm);
    }

    /**
     * Procesa la devolución de un libro asociado a un préstamo.
     * 
     * <p>Si el préstamo aún no se ha devuelto, se registra la fecha de devolución y se actualiza la disponibilidad del libro.</p>
     * 
     * <p>En caso de que el préstamo ya haya sido devuelto o no se encuentre, se lanzará una excepción.</p>
     * 
     * <p>Este método es transaccional para garantizar la consistencia de las operaciones relacionadas con el préstamo y el libro.</p>
     * 
     * @param lendingId Identificador único del préstamo a procesar.
     * @throws IllegalStateException Si el préstamo ya ha sido devuelto.
     * @throws IllegalArgumentException Si el préstamo no se encuentra.
     */
    @Override
    @Transactional
    public void processReturn(Integer lendingId) {
        Optional<Lending> optionalLending = lendingRepository.findById(lendingId);
        if (optionalLending.isPresent()) {
            Lending lending = optionalLending.get();
            if (lending.getDateReturn() == null) {
                lending.setDateReturn(java.time.LocalDate.now());
                lendingRepository.save(lending);

                // Actualizar la disponibilidad del libro
                Book book = lending.getBook();
                book.setAvailable(book.getAvailable() + 1);
                bookService.saveBook(book);
            } else {
                throw new IllegalStateException("El préstamo ya ha sido devuelto.");
            }
        } else {
            throw new IllegalArgumentException("Préstamo no encontrado.");
        }
    }
}

