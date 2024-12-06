package mycompany.LibrarySystem.controller;

import java.time.LocalDate;
import mycompany.LibrarySystem.model.entities.Lending;
import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.entities.User;
import mycompany.LibrarySystem.model.services.LendingService;
import mycompany.LibrarySystem.model.services.BookService;
import mycompany.LibrarySystem.model.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador que gestiona los préstamos almacenados en la base de datos del sistema.
 * 
 * <p>Proporciona funcionalidades para listar, crear, editar, eliminar y guardar préstamos. 
 * Este controlador interactúa con los servicios {@link LendingService}, {@link BookService},
 * y {@link UserService} para realizar las operaciones correspondientes en la base de datos.</p>
 * 
 * <p>Los datos se envían a las vistas utilizando el objeto {@link Model}, lo que permite 
 * renderizar la interfaz de usuario con información actualizada.</p>
 * 
 * @author David Escalante
 * @version 02/12/2024
 */
@Controller
@RequestMapping("/lendings")
public class LendingsController {

    private final LendingService lendingService;
    private final BookService bookService;
    private final UserService userService;

    /**
     * Constructor que inyecta las dependencias {@link LendingService}, {@link BookService}, y {@link UserService}.
     * 
     * @param lendingService Servicio que proporciona acceso a las operaciones de préstamos.
     * @param bookService Servicio que proporciona acceso a las operaciones de libros.
     * @param userService Servicio que proporciona acceso a las operaciones de usuarios.
     */
    @Autowired
    public LendingsController(LendingService lendingService, BookService bookService, UserService userService) {
        this.lendingService = lendingService;
        this.bookService = bookService;
        this.userService = userService;
    }

    /**
     * Maneja solicitudes HTTP GET para listar los préstamos del sistema.
     * 
     * <p>Recupera todos los préstamos registrados y los envía a la vista para su visualización.</p>
     * 
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizarse, en este caso, {@code "lendings/list"}.
     */
    @GetMapping
    public String listLendings(Model model) {
        List<Lending> lendings = lendingService.findAllLendings();
        model.addAttribute("lendings", lendings);
        return "lendings/list";
    }

    /**
     * Muestra el formulario para crear un nuevo préstamo.
     * 
     * <p>Incluye la fecha actual como fecha de salida, además de cargar los datos de todos los 
     * libros y usuarios disponibles.</p>
     * 
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizarse, en este caso, {@code "lendings/form"}.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Lending lending = new Lending();
        lending.setDateOut(LocalDate.now());
        model.addAttribute("lending", lending);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "lendings/form";
    }

    /**
     * Guarda un nuevo préstamo o actualiza uno existente en el sistema.
     * 
     * <p>Valida que tanto el libro como el usuario existan antes de guardar el préstamo.
     * En caso de error, se notifica al usuario mediante mensajes flash.</p>
     * 
     * @param lending Objeto {@link Lending} recibido desde el formulario de la vista.
     * @param redirectAttributes Objeto {@link RedirectAttributes} utilizado para pasar mensajes de éxito o error.
     * @return Redirección a la lista de préstamos o al formulario de creación si ocurre un error.
     */
    @PostMapping("/save")
    public String saveLending(@ModelAttribute("lending") Lending lending, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.findBookById(lending.getBook().getId());
            if (book == null) {
                redirectAttributes.addFlashAttribute("error", "El libro seleccionado no existe.");
                return "redirect:/lendings/new";
            }

            User user = userService.findUserById(lending.getUser().getId());
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "El usuario seleccionado no existe.");
                return "redirect:/lendings/new";
            }

            lending.setDateOut(LocalDate.now());
            lending.setBook(book);
            lending.setUser(user);
            lendingService.saveLending(lending);
            redirectAttributes.addFlashAttribute("message", "Préstamo guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el préstamo: " + e.getMessage());
            return "redirect:/lendings/new";
        }
        return "redirect:/lendings";
    }

    /**
     * Elimina un préstamo del sistema.
     * 
     * <p>Si la operación es exitosa, se muestra un mensaje de confirmación. En caso de error,
     * se notifica al usuario.</p>
     * 
     * @param id Identificador único del préstamo a eliminar.
     * @param redirectAttributes Objeto {@link RedirectAttributes} utilizado para pasar mensajes de éxito o error.
     * @return Redirección a la lista de préstamos.
     */
    @GetMapping("/delete/{id}")
    public String deleteLending(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            lendingService.deleteLending(id);
            redirectAttributes.addFlashAttribute("message", "Préstamo eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el préstamo: " + e.getMessage());
        }
        return "redirect:/lendings";
    }

    /**
     * Muestra el formulario para editar un préstamo existente.
     * 
     * <p>Si el préstamo no existe, redirige a la lista de préstamos. Si existe, carga los datos
     * necesarios para la edición, incluyendo la lista de libros y usuarios disponibles.</p>
     * 
     * @param id Identificador único del préstamo a editar.
     * @param model Modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizarse, o redirección a la lista de préstamos si el préstamo no existe.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Lending lending = lendingService.findLendingById(id);
        if (lending == null) {
            return "redirect:/lendings";
        }
        model.addAttribute("lending", lending);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "lendings/form";
    }
}

