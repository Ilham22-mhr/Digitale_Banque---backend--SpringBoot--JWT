package com.bank.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("CE")
@Data
@NoArgsConstructor
public class CompteEpargne extends CompteBancaire {

    private double tauxInteret;


    public CompteEpargne(Long id, Date dateCreation, double solde,
                         StatCompte statut, String devise, Client client,
                         List<Operation> operations, String createdBy,
                         String lastModifiedBy, double tauxInteret) {
        super(id, dateCreation, solde, statut, devise, client, operations, createdBy, lastModifiedBy);
        this.tauxInteret = tauxInteret;
    }
}