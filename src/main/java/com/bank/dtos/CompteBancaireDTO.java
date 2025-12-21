package com.bank.dtos;

import com.bank.entities.StatCompte;
import lombok.Data;

import java.util.Date;

@Data
public class CompteBancaireDTO {
    private Long id;
    private Date dateCreation;
    private double solde;
    private StatCompte statut;
    private String devise;
    private String typeCompte;
    private Long clientId;
    private String clientNom;
    private Double decouvert;
    private Double tauxInteret;
}