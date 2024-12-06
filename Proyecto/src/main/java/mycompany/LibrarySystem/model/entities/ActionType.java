// src/main/java/mycompany/SpringPruebaMVC/model/entities/ActionType.java

package mycompany.LibrarySystem.model.entities;


/**
 * Enum que define los diferentes tipos de acciones realizadas en el sistema.
 * 
 * <p>Este enum se utiliza para categorizar las acciones que pueden realizarse, como 
 * préstamos, devoluciones, y la gestión de libros y usuarios.</p>
 * 
 * <ul>
 *     <li><strong>LOAN:</strong> Acción de préstamo de un libro.</li>
 *     <li><strong>RETURN:</strong> Acción de devolución de un libro.</li>
 *     <li><strong>NEW_BOOK:</strong> Acción de agregar un nuevo libro al sistema.</li>
 *     <li><strong>DELETE_BOOK:</strong> Acción de eliminar un libro del sistema.</li>
 *     <li><strong>NEW_USER:</strong> Acción de agregar un nuevo usuario al sistema.</li>
 *     <li><strong>DELETE_USER:</strong> Acción de eliminar un usuario del sistema.</li>
 * </ul>
 * 
 * @author César Miam 
 * @version 01/12/2024
 */
public enum ActionType {
    LOAN,
    RETURN,
    NEW_BOOK,
    DELETE_BOOK,
    NEW_USER,
    DELETE_USER
}
