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
 *Controlador que gestiona los usuarios almacenados en la base de datos del sistema.
 * 
 * @author José Murcia
 * @version 02/12/24
 */
@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    
    /**
     *Constructor que inyecta el servicio de usuarios.
     * 
     * @param userService servicio que proporciona acceso a las operaciones de usuario.
     */
    @Autowired 
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    /**
     *Lista los usuarios almacenados en el sistema.
     * 
     * 
     * @param search cadena de busqueda que se ingresa, en caso de ser {@code null}, se mostraran todos los
     * usuarios.
     * @param model modelo utilizado para pasar datos a la vista.
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
     *Muestra el formulario para crear usuarios
     * 
     * @param model modelo utilizado para pasar datos a la vista.
     * @return El nombre de la vista {@code "users/form"}.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "users/form";
    }

    /**
     *Agrega un nuevo usuario al sistema.
     * 
     * @param user usuario a agregar.
     * @return Redirección a la lista de usuarios.
     */
    @PostMapping("/save")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.saveUser(user);
        return "redirect:/users";
    }

    /**
     *Muestra el formulario de edicion para un usuario en especifico.
     * 
     * @param id identificador unico del usuario a editar.
     * @param model modelo utilizado para pasar datos a la vista.
     * @return Si el usuario no existe se da una redirección a la lista de usuarios, en caso contrario, se retorna el
     * nombre de la vista {@code "users/form"}.
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
     *Elimina un usuario del sistema.
     * 
     * @param id identificador unico del usuario.
     * @return Redirección a la lista de usuarios.
     */
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
    
    /**
     *Elimina varios usuarios seleccionados del sistema.
     * 
     * <p>En caso de no seleccionar usuarios se muestra un mensaje de advertencia y se redirigirá a la lista de usuarios,
     * en caso contrario, intentará eliminar los usuarios seleccionados y gestionara posibles errores.</p>
     * 
     * @param selectedUsers lista de identificadores de usuarios.
     * @param redirectAttributes atributos para enviar mensajes de estado entre redirecciones.
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
