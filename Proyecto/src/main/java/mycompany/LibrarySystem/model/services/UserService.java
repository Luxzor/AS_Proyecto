package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.User;
import mycompany.LibrarySystem.model.entities.Book;

import java.util.List;

/**
 *Interfaz que define los servicios relacionados con la gestión de libros del sistema.
 * 
 * <p>Actua como un contrato para la capa de servicios, proporcionando metodos para realizar operaciones  
 * sobre los usuarios, como encontrar uno o todos, guardar, eliminar, buscar por cadena en nombre y eliminar varios.</p>
 * 
 * @author David Escalante
 * @version 01/12/2024
 */
public interface UserService {
    /**
     *Obtiene una lista de todos los usuarios registrados en la base de datos del sistema.
     * 
     * @return Lista de objetos {@link Book} que representan los libros existentes.
     */
    List<User> findAllUsers();
    /**
     *Devuelve un usuario del sistema segun su id.
     * 
     * @param id identificador unico del usuario.
     * @return Usuario que coincide con la id del parametro
     */
    User findUserById(Integer id);
    /**
     *Guarda un usuario en el sistema.
     * 
     * @param user objeto {@link User} que se desea guardar.
     * @return Objeto {@link User} guardado.
     */
    User saveUser(User user);
    /**
     *Elimina un usuario segun su id.
     * 
     * @param id identificador unico del usuario a eliminar.
     */
    void deleteUser(Integer id);
    
    // Nuevo método para buscar usuarios por nombre
    /**
     *Devuelve una lista de libros que coincidan con una cadena especifica.
     * 
     * @param name la cadena parcial o total a buscar en los titulos de los usuarios de la lista. 
     * @return Lista de usuarios cuyos nombres contienen la cadena.
     */
    List<User> findUsersByNameContaining(String name);
    
     /**
     * Elimina múltiples usuarios por sus IDs.
     * 
     * @param userIds Lista de IDs de usuarios a eliminar.
     */
    void bulkDeleteUsers(List<Integer> userIds);
}
