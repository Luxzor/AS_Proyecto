package mycompany.LibrarySystem.model.repositories;

import mycompany.LibrarySystem.model.entities.Lending;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Interfaz que proporciona operaciones CRUD y consultas personalizadas de prestamos.
 * 
 * @author José Murcia
 * @version 01/12/2024
 */
@Repository
public interface LendingRepository extends JpaRepository<Lending, Integer> {

    /**
     *Busca prestamos pendientes de un usuario.
     * 
     * <p>Se hace la búsqueda por nombre, apellido paterno o apellido materno, no
     * distingue entre mayúsculas y minúsculas.</p>
     * 
     * @param searchTerm termino de busqueda, que puede ser tanto del nombre como de algun apellido.
     * @return Lista de prestamos que correspondan con el termino de busqueda.
     */
    @Query("SELECT l FROM Lending l WHERE "
            + "(LOWER(l.user.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(l.user.lastNameP) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(l.user.lastNameM) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND "
            + "l.dateReturn IS NULL")
    List<Lending> searchPendingLendingsByUser(@Param("searchTerm") String searchTerm);

}
