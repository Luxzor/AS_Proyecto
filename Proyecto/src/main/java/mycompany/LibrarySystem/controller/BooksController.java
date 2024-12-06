package mycompany.LibrarySystem.controller;

import mycompany.LibrarySystem.model.entities.Book;
import mycompany.LibrarySystem.model.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *Controlador que gestiona los libros almacenados en la base de datos del sistema. 
 * 
 * <p>Proporciona metodos para listar, crear, editar y eliminar libros.</p>
 * <p>Maneja las interacciones y pasa los datos a las vistas usando el objeto {@link Model}.</p>
 * 
 * @author David Escalante
 * @version 02/12/2024
 *
 */
@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
  private static final Logger logger = LoggerFactory.getLogger(BooksController.class);
  
  /**
     *Constructor que inyecta la dependencia de {@link BookService}
     * 
     * @param bookService servicio utilizado para gestionar las operaciones relacionadas con los libros.
     */
    @Autowired 
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }
    
/**
 *Lista los libros del sistema.
 * 
 * <p>Despliega libros encontrados de acuerdo a coincidencias en el titulo, en caso de no 
 * haber coincidencias muestra todos los libros.</p>
 * 
 * @param search la cadena de busqueda que se ingresa, en caso de que sea vacia, se mostraran todos los
 * libros.
 * @param model el modelo utilizado para pasar datos a la vista. Se añaden los libros encontrados y el término 
 * de búsqueda (si se proporciona).
 * @return El nombre de la vista a renderizarse
 */
    @GetMapping
    public String listBooks(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Book> books;   
        if (search != null && !search.isEmpty()) {
            books = bookService.findBooksByTitleContaining(search);
            model.addAttribute("search", search);
        } else {
            books = bookService.findAllBooks();
        }
        model.addAttribute("books", books);
        return "books/list";
    }

    /**
     *Muestra el formulario de creación de un nuevo libro en el sistema.
     * 
     * <p>Crea un objeto vacio de tipo {@link Book} y lo añade al modelo para que la vista lo use como base para un
     * nuevo libro.</p>
     * 
     * @param model modelo utilizado para pasar datos a la vista. Se añaden los libros encontrados y el término 
     * de búsqueda (si se proporciona).
     * @return Nombre de la vista a renderizarse, en este caso, el formulario de libros.
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Book book = new Book();
        model.addAttribute("book", book);
        return "books/form";
    }

    /**
     *Guarda un nuevo libro en el sistema o actualiza uno existente.
     * 
     * @param book objeto {@link Book} recibido desde la vista.
     * @return Una redirección a la lista de libros. 
     */
    @PostMapping("/save")
    public String saveBook(@ModelAttribute("book") Book book) {
        bookService.saveBook(book);
        return "redirect:/books";
    }

    /**
     *Muestra el formulario de edición de un libro del sistema.
     * 
     * <p>Busca un libro por su ID y lo añade al modelo para que la vista pueda mostrar sus datos 
     * preexistentes en el formulario.</p>
     * 
     * @param id identificador unico del libro a editar.
     * @param model modelo utilizado para pasar datos a la vista.
     * @return Nombre de la vista a renderizar, en caso de que exista el libro, redirige al formulario, 
     * en caso contrario redirige a la lista de libros.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.findBookById(id);
        if (book == null) {
  
            return "redirect:/books";
        }
        model.addAttribute("book", book);
        return "books/form";
    }

    /**
     *Elimina un libro del sistema.
     * 
     * @param id identificador unico del libro a eliminar.
     * @return Redirección a la lista de libros.
     */
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Integer id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
    
    /**
     *Elimina varios libros seleccionados del sistema.
     * 
     * <p>En caso de no seleccionar libros se muestra un mensaje de advertencia y se redirigirá a la lista de libros,
     * en caso contrario, intentará eliminar los libros seleccionados y gestionara posibles errores.</p>
     * 
     * @param selectedBooks lista de identficadores de libros.
     * @param redirectAttributes atributos para enviar mensajes de estado entre redirecciones.
     * @return Redirección a la lista de libros.
     */
    @PostMapping("/bulkDelete")
    public String bulkDeleteBooks(@RequestParam(value = "selectedBooks", required = false) List<Integer> selectedBooks,
                                  RedirectAttributes redirectAttributes) {
        if (selectedBooks == null || selectedBooks.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "No se seleccionaron libros para eliminar.");
            logger.warn("Intento de eliminación masiva sin libros seleccionados.");
            return "redirect:/books";
        }

        try {
            bookService.bulkDeleteBooks(selectedBooks);
            redirectAttributes.addFlashAttribute("message", "Libros eliminados exitosamente.");
            logger.info("Eliminados libros con IDs: {}", selectedBooks);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar los libros: " + e.getMessage());
            logger.error("Error al eliminar libros con IDs {}: {}", selectedBooks, e.getMessage());
        }

        return "redirect:/books";
    }
}
