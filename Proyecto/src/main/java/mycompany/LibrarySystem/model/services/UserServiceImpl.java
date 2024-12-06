package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.User;
import mycompany.LibrarySystem.model.entities.ActionType;
import mycompany.LibrarySystem.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Clase que implementa los métodos definidos en la interfaz {@link UserService} para la gestión de usuarios.
 * 
 * <p>Esta clase actúa como la capa de servicio para realizar operaciones CRUD relacionadas con los usuarios 
 * registrados en el sistema. También incluye validaciones específicas, como la verificación de préstamos pendientes 
 * antes de eliminar un usuario.</p>
 * 
 * @author Sebastian Laines
 * @version 02/12/2024
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReportEntryService reportEntryService;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param userRepository Repositorio que proporciona acceso a las operaciones de base de datos relacionadas con usuarios.
     * @param reportEntryService Servicio para registrar acciones importantes como creación o eliminación de usuarios.
     */
    @Autowired
    public UserServiceImpl(UserRepository userRepository, ReportEntryService reportEntryService) {
        this.userRepository = userRepository;
        this.reportEntryService = reportEntryService;
    }

    /**
     * Devuelve todos los usuarios registrados en el sistema.
     * 
     * @return Lista de todos los usuarios.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Busca y devuelve un usuario por su identificador único.
     * 
     * @param id Identificador único del usuario.
     * @return El usuario correspondiente al identificador, o {@code null} si no se encuentra.
     */
    @Override
    public User findUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    /**
     * Guarda o actualiza un usuario en el sistema.
     * 
     * <p>Además, registra un reporte indicando que un nuevo usuario ha sido creado.</p>
     * 
     * <p>Este método es transaccional, lo que asegura que cualquier fallo revierta los cambios realizados durante 
     * la operación.</p>
     * 
     * @param user Objeto {@link User} que se desea guardar o actualizar.
     * @return El usuario guardado.
     */
    @Override
    @Transactional
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        reportEntryService.createReport(
            ActionType.NEW_USER,
            "Se agregó el usuario: " + savedUser.getName(),
            savedUser.getId()
        );
        return savedUser;
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * <p>Si el usuario tiene préstamos pendientes, no se permite la eliminación y se lanza una excepción. 
     * En caso contrario, elimina el usuario y registra un reporte de la acción.</p>
     * 
     * <p>Este método es transaccional, lo que asegura la reversión de cambios en caso de error.</p>
     * 
     * @param id Identificador único del usuario a eliminar.
     */
    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            if (user.getLendings() != null && !user.getLendings().isEmpty()) {
                throw new IllegalStateException("El usuario '" + user.getName() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            userRepository.deleteById(id);
            reportEntryService.createReport(
                ActionType.DELETE_USER,
                "Se eliminó el usuario: " + user.getName(),
                id
            );
        }
    }

    /**
     * Busca usuarios cuyos nombres contengan una cadena específica, sin importar mayúsculas o minúsculas.
     * 
     * @param name Cadena parcial o total a buscar en los nombres de los usuarios.
     * @return Lista de usuarios cuyos nombres coincidan con la cadena proporcionada.
     */
    @Override
    public List<User> findUsersByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Elimina múltiples usuarios seleccionados.
     * 
     * <p>Antes de la eliminación, valida que todos los usuarios existan y que ninguno tenga préstamos pendientes. 
     * Si se detecta algún usuario con préstamos pendientes, se lanza una excepción y no se realiza la eliminación.</p>
     * 
     * <p>Registra un reporte por cada usuario eliminado exitosamente.</p>
     * 
     * <p>Este método es transaccional, lo que asegura la reversión de cambios en caso de error.</p>
     * 
     * @param userIds Lista de identificadores únicos de los usuarios a eliminar.
     * @throws IllegalArgumentException Si no se encuentran todos los usuarios especificados.
     * @throws IllegalStateException Si algún usuario tiene préstamos pendientes.
     */
    @Override
    @Transactional
    public void bulkDeleteUsers(List<Integer> userIds) {
        List<User> usersToDelete = userRepository.findAllById(userIds);
        if (usersToDelete.size() != userIds.size()) {
            throw new IllegalArgumentException("Algunos usuarios no fueron encontrados para eliminar.");
        }
        for (User user : usersToDelete) {
            if (user.getLendings() != null && !user.getLendings().isEmpty()) {
                throw new IllegalStateException("El usuario '" + user.getName() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            reportEntryService.createReport(
                ActionType.DELETE_USER,
                "Se eliminó el usuario: " + user.getName(),
                user.getId()
            );
        }
        userRepository.deleteAll(usersToDelete);
    }
}

