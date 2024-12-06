// src/main/java/mycompany/SpringPruebaMVC/model/services/LendingService.java

package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.Lending;
import java.util.List;

/**
 *Interfaz que define los servicios relacionados con la gestión de prestamos del sistema.
 * 
 * <p>Actua como un contrato para la capa de servicios, proporcionando metodos para realizar operaciones  
 * sobre los prestamos, como encontrar uno o todos, guardar y eliminar</p>
 * 
 * @author Luis Montero
 * @version 02/12/2024
 */
public interface LendingService {

    /**
     *Obtiene una lista de todos los prestamos registrados en la base de datos del sistema.
     * 
     * @return Lista de objetos {@link Lending} que representan los prestamos existentes.
     */
    List<Lending> findAllLendings();

    /**
     *Devuelve un prestamo del sistema segun su id.
     * 
     * @param id identificador unico del prestamo.
     * @return Libro que coincide con la id del parametro
     */
    Lending findLendingById(Integer id);

    /**
     *Guarda un prestamo en el sistema.
     * 
     * @param lending objeto {@link Lending} que se desea guardar.
     * @return Objeto {@link Lending} guardado.
     */
    Lending saveLending(Lending lending);

    /**
     *Elimina un prestamo segun su id.
     * 
     * @param id identificador unico del prestmao a eliminar.
     */
    void deleteLending(Integer id);
    
    // Método actualizado para búsqueda filtrada

    /**
     *Busca los prestamos pendientes de un usuario.
     * 
     * @param searchTerm termino por el cual se buscara el prestamo.
     * @return Lista de prestamos que coincidan con la cadena de busqueda.
     */
    List<Lending> searchPendingLendingsByUser(String searchTerm);
    
    /**
     *Procesa la devolución del libro.
     * 
     * @param lendingId identificador unico del prestamo.
     */
    void processReturn(Integer lendingId);
}
