package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.LendingsDetailed;
import mycompany.LibrarySystem.model.repositories.LendingsDetailedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clase que implementa los métodos definidos en la interfaz {@link LendingsDetailedService} para la gestión
 * de préstamos detallados.
 * 
 * <p>Proporciona la lógica de negocio para buscar préstamos pendientes basándose en el nombre de un usuario.
 * Utiliza el repositorio {@link LendingsDetailedRepository} para interactuar con la base de datos.</p>
 * 
 * @author Luis Montero
 * @version 02/12/2024
 */
@Service
public class LendingsDetailedServiceImpl implements LendingsDetailedService {

    private final LendingsDetailedRepository lendingsDetailedRepository;

    /**
     * Constructor que inyecta la dependencia {@link LendingsDetailedRepository}.
     * 
     * @param lendingsDetailedRepository Repositorio que permite realizar consultas relacionadas con préstamos detallados.
     */
    @Autowired
    public LendingsDetailedServiceImpl(LendingsDetailedRepository lendingsDetailedRepository) {
        this.lendingsDetailedRepository = lendingsDetailedRepository;
    }

    /**
     * Busca y devuelve los préstamos pendientes de un usuario según su nombre.
     * 
     * <p>Un préstamo se considera pendiente si no tiene una fecha de devolución registrada.</p>
     * 
     * @param userName El nombre del usuario por el cual se buscarán los préstamos pendientes.
     * @return Lista de objetos {@link LendingsDetailed} que representan los préstamos pendientes del usuario.
     *         Si no se encuentran préstamos pendientes, devuelve una lista vacía.
     */
    @Override
    public List<LendingsDetailed> findPendingLendingsByUserName(String userName) {
        return lendingsDetailedRepository.findByUserNameAndDateReturnIsNull(userName);
    }
}

