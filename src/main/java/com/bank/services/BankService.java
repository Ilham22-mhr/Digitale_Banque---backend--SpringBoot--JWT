
package com.bank.services;

import com.bank.dtos.*;
import com.bank.entities.*;
import com.bank.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
@Service
@Transactional
@RequiredArgsConstructor
public class BankService {


    private final ClientRepository clientRepository;
    private  final  CompteBancaireRepository compteBancaireRepository;
    private  final OperationRepository operationRepository;



    public ClientDTO saveClient(String nom, String email, String username) {
        Client client = new Client();
        client.setNom(nom);
        client.setEmail(email);
        client.setCreatedBy(username);
        client.setLastModifiedBy(username);

        Client saved = clientRepository.save(client);
        return mapToClientDTO(saved);
    }


    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToClientDTO)
                .collect(Collectors.toList());
    }


    public ClientDTO getClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));
        return mapToClientDTO(client);
    }


    public List<ClientDTO> searchClients(String keyword) {
        return clientRepository.findByNomContaining(keyword).stream()
                .map(this::mapToClientDTO)
                .collect(Collectors.toList());
    }

    public void deleteClientWithComptes(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));

        List<CompteBancaire> comptes = compteBancaireRepository.findByClientId(id);

        for (CompteBancaire compte : comptes) {
            operationRepository.deleteAll(operationRepository.findByCompteIdOrderByDateOpDesc(compte.getId()));
        }

        compteBancaireRepository.deleteAll(comptes);

        clientRepository.deleteById(id);
    }





    public CompteBancaireDTO createCompteCourant(Long clientId, double soldeInitial,
                                                 String devise, double decouvert,
                                                 String username) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        CompteCourant compte = new CompteCourant();
        compte.setDateCreation(new Date());
        compte.setSolde(soldeInitial);
        compte.setStatut(StatCompte.CREATED);
        compte.setDevise(devise);
        compte.setClient(client);
        compte.setDecouvert(decouvert);
        compte.setCreatedBy(username);
        compte.setLastModifiedBy(username);

        CompteCourant saved = (CompteCourant) compteBancaireRepository.save(compte);
        return mapToCompteDTO(saved);
    }




    public CompteBancaireDTO createCompteEpargne(Long clientId, double soldeInitial,
                                                 String devise, double tauxInteret,
                                                 String username) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));

        CompteEpargne compte = new CompteEpargne();
        compte.setDateCreation(new Date());
        compte.setSolde(soldeInitial);
        compte.setStatut(StatCompte.CREATED);
        compte.setDevise(devise);
        compte.setClient(client);
        compte.setTauxInteret(tauxInteret);
        compte.setCreatedBy(username);
        compte.setLastModifiedBy(username);

        CompteEpargne saved = (CompteEpargne) compteBancaireRepository.save(compte);
        return mapToCompteDTO(saved);
    }




    public List<CompteBancaireDTO> getAllComptes() {
        return compteBancaireRepository.findAll().stream()
                .map(this::mapToCompteDTO)
                .collect(Collectors.toList());
    }





    public CompteBancaireDTO getCompte(Long id) {
        CompteBancaire compte = compteBancaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        return mapToCompteDTO(compte);
    }






    public List<CompteBancaireDTO> getComptesByClient(Long clientId) {
        return compteBancaireRepository.findByClientId(clientId).stream()
                .map(this::mapToCompteDTO)
                .collect(Collectors.toList());
    }





    public void activerCompte(Long id, String username) {
        CompteBancaire compte = compteBancaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        compte.setStatut(StatCompte.ACTIVATED);
        compte.setLastModifiedBy(username);
        compteBancaireRepository.save(compte);
    }





    public void suspendreCompte(Long id, String username) {
        CompteBancaire compte = compteBancaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));
        compte.setStatut(StatCompte.SUSPENDED);
        compte.setLastModifiedBy(username);
        compteBancaireRepository.save(compte);
    }









    public OperationDTO versement(VersementDTO dto, String username) {
        CompteBancaire compte = compteBancaireRepository.findById(dto.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));


        if (compte.getStatut() != StatCompte.ACTIVATED) {
            throw new RuntimeException("Le compte doit être activé pour effectuer des opérations");
        }


        Operation operation = new Operation();
        operation.setDateOp(new Date());
        operation.setMontant(dto.getMontant());
        operation.setType(TypeOp.CREDIT);
        operation.setDescription(dto.getDescription());
        operation.setCompte(compte);
        operation.setCreatedBy(username);



        compte.setSolde(compte.getSolde() + dto.getMontant());
        compteBancaireRepository.save(compte);

        Operation saved = operationRepository.save(operation);
        return mapToOperationDTO(saved);
    }




    public OperationDTO retrait(RetraitDTO dto, String username) {
        CompteBancaire compte = compteBancaireRepository.findById(dto.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte introuvable"));



        if (compte.getStatut() != StatCompte.ACTIVATED) {
            throw new RuntimeException("Le compte doit être activé pour effectuer des opérations");
        }

        double nouveauSolde = compte.getSolde() - dto.getMontant();




        if (compte instanceof CompteCourant) {
            CompteCourant cc = (CompteCourant) compte;
            if (nouveauSolde < -cc.getDecouvert()) {
                throw new RuntimeException("Découvert dépassé ! Découvert autorisé: " + cc.getDecouvert());
            }
        } else if (nouveauSolde < 0) {
            throw new RuntimeException("Solde insuffisant ! Compte épargne ne peut pas être négatif");
        }

        Operation operation = new Operation();
        operation.setDateOp(new Date());
        operation.setMontant(dto.getMontant());
        operation.setType(TypeOp.DEBIT);
        operation.setDescription(dto.getDescription());
        operation.setCompte(compte);
        operation.setCreatedBy(username);

        compte.setSolde(nouveauSolde);
        compteBancaireRepository.save(compte);

        Operation saved = operationRepository.save(operation);
        return mapToOperationDTO(saved);
    }


    public void virement(VirementDTO dto, String username) {

        RetraitDTO retrait = new RetraitDTO();
        retrait.setCompteId(dto.getCompteSource());
        retrait.setMontant(dto.getMontant());
        retrait.setDescription("Virement vers compte " + dto.getCompteDestination());
        retrait(retrait, username);


        VersementDTO versement = new VersementDTO();
        versement.setCompteId(dto.getCompteDestination());
        versement.setMontant(dto.getMontant());
        versement.setDescription("Virement depuis compte " + dto.getCompteSource());
        versement(versement, username);
    }



    public List<OperationDTO> getOperationsByCompte(Long compteId) {
        return operationRepository.findByCompteIdOrderByDateOpDesc(compteId).stream()
                .map(this::mapToOperationDTO)
                .collect(Collectors.toList());
    }

    public  ClientDTO updateClient(Long id, String nom, String email, String username) {

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable avec l'ID: " + id));


        client.setNom(nom);
        client.setEmail(email);
        client.setLastModifiedBy(username);


        Client updated = clientRepository.save(client);


        return mapToClientDTO(updated);
    }













    // MAPPERS
    private ClientDTO mapToClientDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setNom(client.getNom());
        dto.setEmail(client.getEmail());
        return dto;
    }

    private CompteBancaireDTO mapToCompteDTO(CompteBancaire compte) {
        CompteBancaireDTO dto = new CompteBancaireDTO();
        dto.setId(compte.getId());
        dto.setDateCreation(compte.getDateCreation());
        dto.setSolde(compte.getSolde());
        dto.setStatut(compte.getStatut());
        dto.setDevise(compte.getDevise());
        dto.setClientId(compte.getClient().getId());
        dto.setClientNom(compte.getClient().getNom());

        if (compte instanceof CompteCourant) {
            dto.setTypeCompte("Compte Courant");
            dto.setDecouvert(((CompteCourant) compte).getDecouvert());
        } else if (compte instanceof CompteEpargne) {
            dto.setTypeCompte("Compte Epargne");
            dto.setTauxInteret(((CompteEpargne) compte).getTauxInteret());
        }

        return dto;
    }

    private OperationDTO mapToOperationDTO(Operation operation) {
        OperationDTO dto = new OperationDTO();
        dto.setId(operation.getId());
        dto.setDateOp(operation.getDateOp());
        dto.setMontant(operation.getMontant());
        dto.setType(operation.getType());
        dto.setDescription(operation.getDescription());
        dto.setCompteId(operation.getCompte().getId());
        dto.setCreatedBy(operation.getCreatedBy());
        return dto;
    }
}
