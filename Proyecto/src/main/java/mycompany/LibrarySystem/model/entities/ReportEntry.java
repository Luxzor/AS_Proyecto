// src/main/java/mycompany/SpringPruebaMVC/model/entities/ReportEntry.java

package mycompany.LibrarySystem.model.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;


/**
 *Clase que define el objeto ReportEntry.
 * 
 * @author Luis Montero
 * @version 01/12/24
 */
@Entity
public class ReportEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ActionType actionType;

    private String description;

    private LocalDateTime timestamp;

    // Opcional: ID de la entidad relacionada (e.g., bookId, userId, lendingId)
    private Integer relatedEntityId;

  
    public ReportEntry() {}

    /**
     *Constructor de la clase.
     * 
     * @param actionType
     * @param description
     * @param timestamp
     * @param relatedEntityId
     */
    public ReportEntry(ActionType actionType, String description, LocalDateTime timestamp, Integer relatedEntityId) {
        this.actionType = actionType;
        this.description = description;
        this.timestamp = timestamp;
        this.relatedEntityId = relatedEntityId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Integer relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }
}
