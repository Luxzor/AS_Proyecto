// src/main/java/mycompany/SpringPruebaMVC/model/services/LendingsDetailedServiceImpl.java

package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.LendingsDetailed;
import mycompany.LibrarySystem.model.repositories.LendingsDetailedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *Clase que implementa el metodo de gestion de prestamos detallados definido en la interfaz LendingService.
 * 
 * @author Luis Montero
 * @version 02/12/24
 */
@Service
public class LendingsDetailedServiceImpl implements LendingsDetailedService {

    private final LendingsDetailedRepository lendingsDetailedRepository;

    /**
     *Constructor que inyecta la dependencias {@link lendingsDetailedRepository}.
     * 
     * @param lendingsDetailedRepository
     */
    @Autowired
    public LendingsDetailedServiceImpl(LendingsDetailedRepository lendingsDetailedRepository) {
        this.lendingsDetailedRepository = lendingsDetailedRepository;
    }

    /**
     *Busca y devuelve los préstamos pendientes por usuario segun el nombre de este.
     * 
     * @param userName el nombre de usuario que se usará en la busqueda.
     * @return Lista de préstamos detallados del usuario buscado.
     */
    @Override
    public List<LendingsDetailed> findPendingLendingsByUserName(String userName) {
        return lendingsDetailedRepository.findByUserNameAndDateReturnIsNull(userName);
    }
}
