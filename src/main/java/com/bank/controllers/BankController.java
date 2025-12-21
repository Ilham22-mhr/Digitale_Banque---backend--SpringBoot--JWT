package com.bank.controllers;

import com.bank.dtos.*;
import com.bank.services.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PreAuthorize("hasAuthority('CLIENT_CREATE')")
    @PostMapping("/clients")
    public ClientDTO createClient(@RequestBody ClientDTO clientDTO) {
        return bankService.saveClient(clientDTO.getNom(), clientDTO.getEmail(), "system");
    }


    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @GetMapping("/clients")
    public List<ClientDTO> getAllClients() {
        return bankService.getAllClients();
    }


    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @GetMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id) {
        return bankService.getClient(id);
    }


    @PreAuthorize("hasAuthority('CLIENT_READ')")
    @GetMapping("/clients/search")
    public List<ClientDTO> searchClients(@RequestParam String keyword) {
        return bankService.searchClients(keyword);
    }


    @PreAuthorize("hasAuthority('CLIENT_DELETE')")
    @DeleteMapping("/clients/{id}")
    public void deleteClient(@PathVariable Long id) {
        bankService.deleteClientWithComptes(id);
    }


    @PreAuthorize("hasAuthority('CLIENT_UPDATE')")
    @PutMapping("/clients/{id}")
    public ClientDTO updateClient(@PathVariable Long id, @RequestBody ClientDTO clientDTO) {
        return bankService.updateClient(id, clientDTO.getNom(), clientDTO.getEmail(), "system");
    }




    @PreAuthorize("hasAuthority('COMPTE_CREATE')")
    @PostMapping("/comptes/courant")
    public CompteBancaireDTO createCompteCourant(@RequestBody CompteBancaireDTO dto) {
        return bankService.createCompteCourant(
                dto.getClientId(),
                dto.getSolde(),
                dto.getDevise(),
                dto.getDecouvert() != null ? dto.getDecouvert() : 0.0,
                "system"
        );
    }


    @PreAuthorize("hasAuthority('COMPTE_CREATE')")
    @PostMapping("/comptes/epargne")
    public CompteBancaireDTO createCompteEpargne(@RequestBody CompteBancaireDTO dto) {
        return bankService.createCompteEpargne(
                dto.getClientId(),
                dto.getSolde(),
                dto.getDevise(),
                dto.getTauxInteret() != null ? dto.getTauxInteret() : 0.0,
                "system"
        );
    }


    @PreAuthorize("hasAuthority('COMPTE_READ')")
    @GetMapping("/comptes")
    public List<CompteBancaireDTO> getAllComptes() {
        return bankService.getAllComptes();
    }


    @PreAuthorize("hasAuthority('COMPTE_READ')")
    @GetMapping("/comptes/{id}")
    public CompteBancaireDTO getCompte(@PathVariable Long id) {
        return bankService.getCompte(id);
    }


    @PreAuthorize("hasAuthority('COMPTE_READ')")
    @GetMapping("/comptes/client/{clientId}")
    public List<CompteBancaireDTO> getComptesByClient(@PathVariable Long clientId) {
        return bankService.getComptesByClient(clientId);
    }


    @PreAuthorize("hasAuthority('COMPTE_ACTIVATE')")
    @PutMapping("/comptes/{id}/activer")
    public void activerCompte(@PathVariable Long id) {
        bankService.activerCompte(id, "system");
    }


    @PreAuthorize("hasAuthority('COMPTE_SUSPEND')")
    @PutMapping("/comptes/{id}/suspendre")
    public void suspendreCompte(@PathVariable Long id) {
        bankService.suspendreCompte(id, "system");
    }





    @PreAuthorize("hasAuthority('OPERATION_EXECUTE')")
    @PostMapping("/operations/versement")
    public OperationDTO versement(@RequestBody VersementDTO dto) {
        return bankService.versement(dto, "system");
    }




    @PreAuthorize("hasAuthority('OPERATION_EXECUTE')")
    @PostMapping("/operations/retrait")
    public OperationDTO retrait(@RequestBody RetraitDTO dto) {
        return bankService.retrait(dto, "system");
    }



    @PreAuthorize("hasAuthority('OPERATION_EXECUTE')")
    @PostMapping("/operations/virement")
    public void virement(@RequestBody VirementDTO dto) {
        bankService.virement(dto, "system");
    }


    @PreAuthorize("hasAuthority('OPERATION_EXECUTE')")
    @GetMapping("/operations/compte/{compteId}")
    public List<OperationDTO> getOperationsByCompte(@PathVariable Long compteId) {
        return bankService.getOperationsByCompte(compteId);
    }
}