package mycompany.LibrarySystem.model.repositories;

import mycompany.LibrarySystem.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interfaz que proporciona operaciones CRUD y consultas personalizadas de usuarios.
 * 
 * @author José Murcia
 * @version 30/11/2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Método de consulta personalizado para buscar por nombre
    List<User> findByNameContainingIgnoreCase(String name);
}
