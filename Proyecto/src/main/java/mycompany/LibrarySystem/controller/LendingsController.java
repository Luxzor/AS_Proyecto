    
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
 *Controlador que gestiona los prestamos almacenados en la base de datos del sistema.
 * 
 * <p>Permite listar, guardar, crear, eliminar y editar reportes del sistema.</p>
 * <p>Pasa los datos a la vista usando el objeto {@link Model}</p>
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
     *Constructor que inyecta las dependencias {@link lendingService}, {@link bookService}, {@link userService}.
     * 
     * @param lendingService servicio que proporciona acceso a las operaciones de prestamos.
     * @param bookService servicio que proporciona acceso a las operaciones de libros.
     * @param userService servicio que proporciona acceso a las operaciones de usuarios.
     */
    @Autowired
    public LendingsController(LendingService lendingService, BookService bookService, UserService userService) {
        this.lendingService = lendingService;
        this.bookService = bookService;
        this.userService = userService;
    }

    /**
     *Maneja solicitudes HTTP GET para listar prestamos.
     * 
     * <p>Despliega los prestamos encontrados</p>
    * 
     * @param model el modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizarse, en este caso la lista de prestamos {@code "lendings/list"}.
     */
    @GetMapping
    public String listLendings(Model model) {
        List<Lending> lendings = lendingService.findAllLendings();
        model.addAttribute("lendings", lendings);
        return "lendings/list"; // Asegúrate de tener esta vista
    }

    /**
     *Maneja solicitudes HTTP GET para mostrar el formulario de prestamo de un libro.
     * 
     * @param model modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizarse, en este caso {@code "lendings/form"}.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Lending lending = new Lending();
        lending.setDateOut(LocalDate.now());
        model.addAttribute("lending", lending);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("users", userService.findAllUsers());
        return "lendings/form"; // Asegúrate de tener esta vista
    }

    /**
     *Maneja solicitudes HTTP POST para mostrar el guardar el prestamo de un libro.
     * 
     * 
     * @param lending objeto {@link Lending} recibido desde el formulario de la vista.
     * @param redirectAttributes: objeto {@link RedirectAttributes} que se utiliza para pasar mensajes
     * de éxito o error entre redirecciones.
     * @return Si no existe el usuario o el libro, se redirige a {@code "/lendings/new"}, en caso de que 
     * ambos existan, el prestamo se guarda correctamente
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
     *Maneja solicitudes HTTP GET para eliminar un prestamo.
     * 
     * @param id identificador unico del prestamo a eliminar.
     * @param redirectAttributes: objeto {@link RedirectAttributes} que se utiliza para pasar mensajes
     * de éxito o error.
     * @return Redirección a la lista de prestamos {@code "redirect:/lendings"}.
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
     *Maneja solicitudes HTTP GET para editar un prestamo.
     * 
     * @param id identificador unico del prestamo a editar.
     * @param model modelo utilizado para pasar datos a la vista.
     * @return En caso de no encontrar un prestamo con la id proporcionada, se hará una redirección a la lista de
     * de prestamos, en caso contrario, se añaden los atributos a la vista y se renderiza el formulario de prestamos.
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
