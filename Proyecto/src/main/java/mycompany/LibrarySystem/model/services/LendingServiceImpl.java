// src/main/java/mycompany/SpringPruebaMVC/model/services/LendingServiceImpl.java

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
 *Clase que implementa los metodos de gestion de libros definidos en la interfaz LendingService.
 * 
 * @author Luis Montero
 * @version 02/12/24
 */
@Service
public class LendingServiceImpl implements LendingService {

    private final LendingRepository lendingRepository;
    private final BookService bookService;

    /**
     *Constructor que inyecta las dependencias {@link lendingRepository} y {@link bookService}.
     * 
     * @param lendingRepository interfaz que permite el uso de operaciones de gestión de préstamos.
     * @param bookService
     */
    @Autowired
    public LendingServiceImpl(LendingRepository lendingRepository, BookService bookService) {
        this.lendingRepository = lendingRepository;
        this.bookService = bookService;
    }
    
    /**
     *Devuelve todos los préstamos del sistema.
     * 
     * @return Lista de préstamos registrados en el sistema.
     */
    @Override
    public List<Lending> findAllLendings() {
        return lendingRepository.findAll();
    }
    
    /**
     *Busca y devuelve un préstamo segun su identificador.
     * 
     * @param id identificador unico del préstamo.
     * @return Préstamo que corresponda al identificador, en caso de no encontrarse, devolverá {@code null}.
     */
    @Override
    public Lending findLendingById(Integer id) {
        Optional<Lending> optionalLending = lendingRepository.findById(id);
        return optionalLending.orElse(null);
    }
    /**
     *Guarda un préstamo en el sistema.
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param lending libro que se guardara o actualizará.
     * @return Préstamo guardado.
     */
    @Override
    @Transactional
    public Lending saveLending(Lending lending) {
        return lendingRepository.save(lending);
    }
    
    /**
     *Elimina un préstamo del sistema.
     * 
     * @param id identificador unico del préstamo a eliminar.
     */
    @Override
    public void deleteLending(Integer id) {
        lendingRepository.deleteById(id);
    }

    /**
     *Busca y devuelve los prestamos pendientes de un usuario.
     * 
     * @param searchTerm termino por el cual se buscara al usuario (nombre o apellidos).
     * @return Lista de prestamos del usuario cuyo nombre o apellidos coincida con el termino.
     */
    @Override
    public List<Lending> searchPendingLendingsByUser(String searchTerm) {
        return lendingRepository.searchPendingLendingsByUser(searchTerm);
    }

    /**
     *Se procesa un devolución de un libro
     * 
     * <p>Si el libro aun no esta devuelto, guarda la fecha de devolución y cambia su disponibilidad, en caso de que 
     * ya este devuelto, arrojará una excepción al igual que en el caso de no encontrar el libro.</p>
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param lendingId identificador unico del prestamo
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
