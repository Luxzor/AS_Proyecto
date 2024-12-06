package mycompany.LibrarySystem.model.repositories;

import mycompany.LibrarySystem.model.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interfaz que proporciona la operaciones CRUD para gestionar libros.
 * 
 * @author José Murcia
 * @version 02/12/2024
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
     /**
     *Consulta personalizada de libros sin importar las mayúsculas o minúsculas.
     * 
     * @param title titulo a buscar en el sistema.
     * @return Lista de libros que contienen el titulo proporcionado.
     */
    List<Book> findByTitleContainingIgnoreCase(String title);
}
