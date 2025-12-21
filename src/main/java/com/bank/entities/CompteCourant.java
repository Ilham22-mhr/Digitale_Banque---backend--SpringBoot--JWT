package com.bank.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("CC")
@Data
@NoArgsConstructor
public class CompteCourant extends CompteBancaire {

    private double decouvert;


    public CompteCourant(Long id, Date dateCreation, double solde,
                         StatCompte statut, String devise, Client client,
                         List<Operation> operations, String createdBy,
                         String lastModifiedBy, double decouvert) {
        super(id, dateCreation, solde, statut, devise, client, operations, createdBy, lastModifiedBy);
        this.decouvert = decouvert;
    }
}