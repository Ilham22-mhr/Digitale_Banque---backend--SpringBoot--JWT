package com.bank.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOp;

    private double montant;

    @Enumerated(EnumType.STRING)
    private TypeOp type;

    private String description;


    @ManyToOne
    @JoinColumn(name = "compte_id")
    private CompteBancaire compte;

    // Pour la traçabilité
    private String createdBy;
}