package com.bank.repositories;

import com.bank.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {


    Client findByEmail(String email);

    List<Client> findByNomContaining(String keyword);
}