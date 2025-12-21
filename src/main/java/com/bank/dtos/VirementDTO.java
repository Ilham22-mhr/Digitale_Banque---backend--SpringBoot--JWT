package com.bank.dtos;

import lombok.Data;

@Data
public class VirementDTO {
    private Long compteSource;
    private Long compteDestination;
    private double montant;
    private String description;
}