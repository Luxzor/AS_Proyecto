package mycompany.LibrarySystem.controller;

import mycompany.LibrarySystem.model.entities.User;
import mycompany.LibrarySystem.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador que gestiona los usuarios almacenados en la base de datos del sistema.
 * 
 * <p>Proporciona funcionalidades para listar, crear, editar y eliminar usuarios.</p>
 * 
 * <p>Los datos se envían a las vistas utilizando el objeto {@link Model}.</p>
 * 
 * @author José Murcia
 * @version 02/12/2024
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    /**
     * Constructor que inyecta el servicio de usuarios.
     * 
     * @param userService Servicio que proporciona acceso a las operaciones relacionadas con los usuarios.
     */
    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Lista los usuarios almacenados en el sistema.
     * 
     * <p>Si se proporciona una cadena de búsqueda, se listarán únicamente los usuarios cuyos 
     * nombres contengan dicha cadena. En caso contrario, se mostrarán todos los usuarios registrados.</p>
     * 
     * @param search Cadena de búsqueda ingresada por el usuario. Si es {@code null} o vacía, se mostrarán todos los usuarios.
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista {@code "users/list"}.
     */
    @GetMapping
    public String listUsers(@RequestParam(value = "search", required = false) String search, Model model) {
        List<User> users;
        if (search != null && !search.isEmpty()) {
            users = userService.findUsersByNameContaining(search);
            model.addAttribute("search", search);
        } else {
            users = userService.findAllUsers();
        }
        model.addAttribute("users", users);
        return "users/list";
    }

    /**
     * Muestra el formulario para crear un nuevo usuario.
     * 
     * <p>Crea un objeto vacío de tipo {@link User} y lo añade al modelo para que la vista
     * lo utilice como base para un nuevo registro de usuario.</p>
     * 
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista {@code "users/form"}.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "users/form";
    }

    /**
     * Guarda un nuevo usuario en el sistema.
     * 
     * <p>Este método también se utiliza para actualizar un usuario existente si se proporciona
     * un identificador único válido en el objeto {@link User}.</p>
     * 
     * @param user Objeto {@link User} recibido desde el formulario de la vista.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    /**
     * Muestra el formulario de edición para un usuario específico.
     * 
     * <p>Si el usuario no existe, redirige a la lista de usuarios. Si existe, añade los datos 
     * del usuario al modelo para que puedan ser editados en la vista.</p>
     * 
     * @param id Identificador único del usuario a editar.
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista {@code "users/form"} si el usuario existe; en caso contrario,
     * redirección a la lista de usuarios.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        User user = userService.findUserById(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "users/form";
    }

    /**
     * Elimina un usuario del sistema.
     * 
     * <p>Si el usuario no existe, no se realiza ninguna acción.</p>
     * 
     * @param id Identificador único del usuario a eliminar.
     * @return Redirección a la lista de usuarios.
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    /**
     * Elimina varios usuarios seleccionados del sistema.
     * 
     * <p>Si no se seleccionan usuarios, muestra un mensaje de advertencia en la vista. Si se seleccionan 
     * usuarios, intenta eliminarlos y gestiona posibles errores, mostrando mensajes de éxito o error en 
     * la vista.</p>
     * 
     * @param selectedUsers Lista de identificadores de usuarios seleccionados para eliminar.
     * @param redirectAttributes Atributos utilizados para enviar mensajes de estado entre redirecciones.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/bulkDelete")
    public String bulkDeleteUsers(@RequestParam(value = "selectedUsers", required = false) List<Integer> selectedUsers,
                                  RedirectAttributes redirectAttributes) {
        if (selectedUsers == null || selectedUsers.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No se seleccionaron usuarios para eliminar.");
            logger.warn("Intento de eliminación masiva sin usuarios seleccionados.");
            return "redirect:/users";
        }

        try {
            userService.bulkDeleteUsers(selectedUsers);
            redirectAttributes.addFlashAttribute("message", "Usuarios eliminados exitosamente.");
            logger.info("Eliminados usuarios con IDs: {}", selectedUsers);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar los usuarios: " + e.getMessage());
            logger.error("Error al eliminar usuarios con IDs {}: {}", selectedUsers, e.getMessage());
        }

        return "redirect:/users";
    }
}

