/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package mycompany.LibrarySystem.model.services;

import mycompany.LibrarySystem.model.entities.LendingsDetailed;
import java.util.List;

/**
 *Interfaz que define los servicios relacionados con la gestión de prestamos detallados del sistema.
 * 
 * <p>Actua como un contrato para la capa de servicios, proporcionando un metodo para encontrar todos los prestamos pendientes
 * por nombre de usuario</p>
 * 
 * @author Luis Montero
 * @version 02/12/2024
 */
public interface LendingsDetailedService {

    /**
     *Encuentra todos los prestamos pendientes de un usuario.
     * 
     * @param userName nombre del usuario  por el cual se buscaran los prestamos.
     * @return Lista de prestamos detallados.
     */
    List<LendingsDetailed> findPendingLendingsByUserName(String userName);
}