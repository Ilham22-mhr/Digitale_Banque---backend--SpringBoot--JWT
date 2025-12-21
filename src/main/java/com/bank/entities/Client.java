package com.bank.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;


    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    private List<CompteBancaire> comptes;


    private String createdBy;
    private String lastModifiedBy;
}
