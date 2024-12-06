package mycompany.LibrarySystem.model.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;


/**
 *Clase que define el objeto Book.
 * 
 * @author César Miam
 * @version 01/12/24
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    
    private String date;

    private String author;

    private String category;

    
    private String edit;

    private String lang;

    private String pages;

    private String description;

    private Integer stock;

    private Integer available;
    
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lending> lendings = new ArrayList<>();

 
    public Book() {
    }

    /**
     *Constructor de la clase.
     * 
     * @param title titulo del libro.
     * @param date fecha de publicación del libro.
     * @param author autor del libro.
     * @param category categoria/genero del libro.
     * @param edit editorial del libro.
     * @param lang lenguaje en que esta escrito el libro.
     * @param pages cantidad de paginas que tiene el libro.
     * @param description descripcion del libro.
     * @param ejemplares numero de ejemplares del libro.
     * @param stock numero de libros en stock.
     * @param available numero de libros disponibles.
     */
    public Book(String title, String date, String author, String category, String edit, String lang, String pages, String description, String ejemplares, Integer stock, Integer available) {
        this.title = title;
        this.date = date;
        this.author = author;
        this.category = category;
        this.edit = edit;
        this.lang = lang;
        this.pages = pages;
        this.description = description;
        this.stock = stock;
        this.available = available;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getEdit() {
        return edit;
    }

    public void setEdit(String edit) {
        this.edit = edit;
    }
    
    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
    
    public List<Lending> getLendings() {
        return lendings;
    }

    public void setLendings(List<Lending> lendings) {
        this.lendings = lendings;
    }
}
