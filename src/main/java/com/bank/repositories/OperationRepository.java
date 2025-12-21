package com.bank.repositories;

import com.bank.entities.Operation;
import com.bank.entities.TypeOp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {


    List<Operation> findByCompteId(Long compteId);


    @Query("SELECT o FROM Operation o WHERE o.compte.id = :compteId ORDER BY o.dateOp DESC")
    List<Operation> findByCompteIdOrderByDateOpDesc(@Param("compteId") Long compteId);

    @Query("SELECT SUM(o.montant) FROM Operation o WHERE o.type = :type")
    Double sumByType(@Param("type") TypeOp type);


    @Query("SELECT o FROM Operation o ORDER BY o.dateOp DESC")
    List<Operation> findAllOrderByDateOpDesc();
}