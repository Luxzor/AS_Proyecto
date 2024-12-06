package mycompany.LibrarySystem.model.repositories;

import mycompany.LibrarySystem.model.entities.LendingsDetailed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


/**
 *Interfaz que proporciona el metodo para encontrar usuarios deudores
 * 
 * @author José Murcia
 * @version 01/12/2024
 */
@Repository
public interface LendingsDetailedRepository extends JpaRepository<LendingsDetailed, Integer> {
    List<LendingsDetailed> findByUserNameAndDateReturnIsNull(String userName);
}
