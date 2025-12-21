package com.bank.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TYPE_COMPTE", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
public abstract class CompteBancaire  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    private Date dateCreation;

    private double solde;

    @Enumerated(EnumType.STRING)
    private StatCompte statut;

    private String devise;


    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Un compte peut avoir plusieurs opérations
    @OneToMany(mappedBy = "compte", fetch = FetchType.LAZY)
    private List<Operation> operations;

    // Pour la traçabilité
    private String createdBy;
    private String lastModifiedBy;

    // Constructeur avec tous les paramètres
    public CompteBancaire(Long id, Date dateCreation, double solde,
                          StatCompte statut, String devise, Client client,
                          List<Operation> operations, String createdBy,
                          String lastModifiedBy) {
        this.id = id;
        this.dateCreation = dateCreation;
        this.solde = solde;
        this.statut = statut;
        this.devise = devise;
        this.client = client;
        this.operations = operations;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }
}
