package com.bank.dtos;

import lombok.Data;

@Data
public class RetraitDTO {
    private Long compteId;
    private double montant;
    private String description;
}
