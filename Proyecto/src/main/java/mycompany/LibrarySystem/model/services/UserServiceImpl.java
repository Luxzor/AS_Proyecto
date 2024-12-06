// src/main/java/mycompany/SpringPruebaMVC/model/services/UserServiceImpl.java

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
 *Clase que implementa los metodos de gestion de usuarios definidos en la interfaz UserService.
 * 
 * @author José Antonio
 * @version 02/12/24
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ReportEntryService reportEntryService;

    /**
     *Constructor que inyecta las dependencias {@link userRepository} y {@link ReportEntryService}.
     * 
     * @param userRepository interfaz que permite la busqueda de usuario si contiene una cadena y ignorando mayúsculas y minúsculas.
     * @param reportEntryService interfaz que permite el uso de la operación de busqueda por rango de tiempo.
     */
    @Autowired // Inyección por constructor
    public UserServiceImpl(UserRepository userRepository, ReportEntryService reportEntryService) {
        this.userRepository = userRepository;
        this.reportEntryService = reportEntryService;
    }

    /**
     *Devuelve todos los usuarios del sistema.
     * 
     * @return Lista de usuarios registrados en el sistema.
     */
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    /**
     *Busca y devuelve un usuario segun su identificador.
     * 
     * @param id identificador unico del usuario.
     * @return Usuario que corresponda al identificador, en caso de no encontrarse, devolverá {@code null}.
     */
    @Override
    public User findUserById(Integer id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    /**
     *Guarda un usuario en el sistema.
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param user usuario que se guardara o actualizará.
     * @return Usuario guardado.
     */
    @Override
    @Transactional
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);
        // Crear reporte de nuevo usuario
        reportEntryService.createReport(
            ActionType.NEW_USER,
            "Se agregó el usuario: " + savedUser.getName(),
            savedUser.getId()
        );
        return savedUser;
    }

    /**
     *Elimina un usuario del sistema.
     * 
     * <p>En caso de que el usuario tenga prestamos pendientes, lo notificará y arrojará una excepción. En caso
     * contrario eliminará el usuario y creará un reporte de la acción.</p>
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param id identificador unico del usuario a eliminar.
     */
    @Override
    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            // Verificar si el usuario tiene préstamos pendientes
            if (user.getLendings() != null && !user.getLendings().isEmpty()) {
                throw new IllegalStateException("El usuario '" + user.getName() + "' tiene préstamos pendientes y no puede ser eliminado.");
            }
            userRepository.deleteById(id);
            // Crear reporte de eliminación de usuario
            reportEntryService.createReport(
                ActionType.DELETE_USER,
                "Se eliminó el usuario: " + user.getName(),
                id
            );
        }
    }

    /**
     *Encuentra a los usuarios que contengan una cadena proporcionada en el nombre.
     * 
     * <p>Ignora las mayusculas y minusculas.</p>
     * 
     * @param name cadena a buscar en los nombres de los usuarios.
     * @return Lista de usuarios cuyos nombres contengan la cadena proporcionada.
     */
    @Override
    public List<User> findUsersByNameContaining(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }
    
    /**
     *Elimina varios usuarios seleccionados.
     * 
     * <p>En caso de que no se encuentren todos los usuarios a eliminar, arrojará una excepción. En caso contrario intentará
     * eliminar los libros a menos que se encuentre uno con prestamos pendientes, lo que también arrojaría una excepción.</p>
     * 
     * <p>Si ninguno de los usuarios tiene prestamos, se eliminan todos los libros y se crea un reporte de la acción.</p>
     * 
     * <p>Este metodo es transaccional, por lo que se gestiona el inicio, la confirmación y una reversión si es necesaria.</p>
     * 
     * @param userIds lista de indetificadores unicos de los usuarios a eliminar.
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
            // Crear reporte de eliminación de usuario
            reportEntryService.createReport(
                ActionType.DELETE_USER,
                "Se eliminó el usuario: " + user.getName(),
                user.getId()
            );
        }
        userRepository.deleteAll(usersToDelete);
    }
}
