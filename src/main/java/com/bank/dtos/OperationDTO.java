package com.bank.dtos;

import com.bank.entities.TypeOp;
import lombok.Data;

import java.util.Date;

@Data
public class OperationDTO {
    private Long id;
    private Date dateOp;
    private double montant;
    private TypeOp type;
    private String description;
    private Long compteId;
    private String createdBy;
}
