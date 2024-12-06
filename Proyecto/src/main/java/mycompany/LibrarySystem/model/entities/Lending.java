package mycompany.LibrarySystem.model.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

/**
 *Clase que define el objeto Lending.
 * 
 * @author Luis Montero
 * @version 01/12/24
 */
@Entity
@Table(name = "lendings")
public class Lending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) 
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "El usuario es obligatorio.")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "El libro es obligatorio.")  
    private Book book;

    @Column(name = "date_out", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "La fecha de salida es obligatoria.")
    @PastOrPresent(message = "La fecha de salida no puede ser en el futuro.")
    private LocalDate dateOut;

    @Column(name = "date_return")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateReturn;


    public Lending() {
    }

    /**
     *Constructor de la clase.
     * 
     * @param user usuario que hace el prestamo.
     * @param book libro que se presta.
     * @param dateOut fecha de salida del libro.
     * @param dateReturn fecha de devolución del libro.
     */
    public Lending(User user, Book book, LocalDate dateOut, LocalDate dateReturn) {
        this.user = user;
        this.book = book;
        this.dateOut = dateOut;
        this.dateReturn = dateReturn;
    }

 

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getDateOut() {
        return dateOut;
    }

    public void setDateOut(LocalDate dateOut) {
        this.dateOut = dateOut;
    }

    public LocalDate getDateReturn() {
        return dateReturn;
    }

    public void setDateReturn(LocalDate dateReturn) {
        this.dateReturn = dateReturn;
    }
}
