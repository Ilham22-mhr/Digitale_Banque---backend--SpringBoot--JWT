package com.bank.repositories;

import com.bank.entities.CompteBancaire;
import com.bank.entities.StatCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompteBancaireRepository extends JpaRepository<CompteBancaire, Long> {


    List<CompteBancaire> findByClientId(Long clientId);


    @Query("SELECT c FROM CompteBancaire c WHERE c.statut = :statut")
    List<CompteBancaire> findByStatut(@Param("statut") StatCompte statut);


    @Query("SELECT COUNT(c) FROM CompteBancaire c WHERE c.statut = :statut")
    Long countByStatut(@Param("statut") StatCompte statut);


    @Query("SELECT SUM(c.solde) FROM CompteBancaire c")
    Double sumAllSoldes();
}