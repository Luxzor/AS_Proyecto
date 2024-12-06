package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.Book;
import java.util.List;

/**
 *Interfaz que define los servicios relacionados con la gestión de libros del sistema.
 * 
 * <p>Actua como un contrato para la capa de servicios, proporcionando metodos para realizar operaciones  
 * sobre los libros, como encontrar uno o todos, guardar y eliminar</p>
 * 
 * @author Luis Montero
 * @version 02/12/2024
 */
public interface BookService {
    
    /**
     *Obtiene una lista de todos los libros registrados en la base de datos del sistema.
     * 
     * @return una lista de objetos {@link Book} que representan los libros existentes.
     */
    List<Book> findAllBooks();

    /**
     *Devuelve un libro del sistema segun su id.
     * 
     * @param id identificador unico del libro.
     * @return Libro que coincide con la id del parametro
     */
    Book findBookById(Integer id);

    /**
     *Guarda un libro en el sistema.
     * 
     * @param book objeto {@link Book} que se desea guardar.
     * @return Objeto {@link Book} guardado.
     */
    Book saveBook(Book book);

    /**
     *Elimina un libro segun su id.
     * 
     * @param id Identificador unico del libro a eliminar.
     */
    void deleteBook(Integer id);
    
    // Nuevo método para buscar libros por título

    /**
     *Devuelve una lista de libros que coincidan con una cadena especifica.
     * 
     * @param title la cadena parcial o total a buscar en los titulos de los libros de la lista.
     * @return Lista de libros cuyos nombres contienen la cadena.
     */
    List<Book> findBooksByTitleContaining(String title);
    
    /**
     * Elimina múltiples libros por sus IDs.
     * 
     * @param bookIds lista de IDs de libros a eliminar.
     */
    void bulkDeleteBooks(List<Integer> bookIds);
}
