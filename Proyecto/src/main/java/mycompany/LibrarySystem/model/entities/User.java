/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mycompany.LibrarySystem.model.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 *Clase que define el objeto User.
 * 
 * @author José Murcia
 * @version 30/11/2024
 */
@Entity
@Table(name= "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "last_name_p")
    private String lastNameP;

    @Column(name = "last_name_m")
    private String lastNameM;

    private String domicilio;

    private String tel;

 
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lending> lendings = new ArrayList<>();

    public User() {
    }

    /**
     *Constructor de la clase.
     * 
     * @param name nombre del usuario.
     * @param lastNameP apellido paterno del usuario.
     * @param lastNameM apellido materno del usuario.
     * @param domicilio domicilio del usuario.
     * @param tel telefono del usuario.

     */
    public User(String name, String lastNameP, String lastNameM, String domicilio, String tel) {
        this.name = name;
        this.lastNameP = lastNameP;
        this.lastNameM = lastNameM;
        this.domicilio = domicilio;
        this.tel = tel;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastNameP() {
        return lastNameP;
    }

    public void setLastNameP(String lastNameP) {
        this.lastNameP = lastNameP;
    }

    public String getLastNameM() {
        return lastNameM;
    }

    public void setLastNameM(String lastNameM) {
        this.lastNameM = lastNameM;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<Lending> getLendings() {
        return lendings;
    }

    public void setLendings(List<Lending> lendings) {
        this.lendings = lendings;
    }

    
}

