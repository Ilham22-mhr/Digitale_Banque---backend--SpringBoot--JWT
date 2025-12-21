
package com.bank;

import com.bank.entities.*;
import com.bank.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TAF_Banque_DigitaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TAF_Banque_DigitaleApplication.class, args);

        System.out.println("\n========================================");
        System.out.println("Application Banque Digitale démarrée !");
        System.out.println("========================================");
        System.out.println("API disponible sur: http://localhost:8091/api");
        System.out.println("🗄 Base de données: MariaDB (bankdb)");
        System.out.println("========================================\n");
    }

    @Bean
    CommandLineRunner initData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthorityRepository authorityRepository,
            ClientRepository clientRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.count() > 0) {
                System.out.println("✓ Données déjà initialisées");
                return;
            }

            System.out.println("\n========================================");
            System.out.println(" Initialisation des rôles et authorities...");
            System.out.println("========================================");


            Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));

            authorityRepository.save(new Authority("CLIENT_READ", "Consulter les clients", adminRole));
            authorityRepository.save(new Authority("CLIENT_DELETE", "Supprimer les clients", adminRole));

            authorityRepository.save(new Authority("COMPTE_READ", "Consulter les comptes", adminRole));
            authorityRepository.save(new Authority("COMPTE_ACTIVATE", "Activer les comptes", adminRole));
            authorityRepository.save(new Authority("COMPTE_SUSPEND", "Suspendre les comptes", adminRole));

            System.out.println("✅ ROLE_ADMIN créé avec authorities (consulter/supprimer clients, consulter/activer/suspendre comptes)");


            Role employeRole = roleRepository.save(new Role("ROLE_EMPLOYE"));

            authorityRepository.save(new Authority("CLIENT_READ", "Consulter les clients", employeRole));
            authorityRepository.save(new Authority("CLIENT_CREATE", "Créer les clients", employeRole));
            authorityRepository.save(new Authority("CLIENT_UPDATE", "Modifier les clients", employeRole));

            authorityRepository.save(new Authority("COMPTE_READ", "Consulter les comptes", employeRole));
            authorityRepository.save(new Authority("COMPTE_CREATE", "Créer les comptes", employeRole));

            authorityRepository.save(new Authority("OPERATION_EXECUTE", "Faire des opérations", employeRole));

            System.out.println("✅ ROLE_EMPLOYE créé avec authorities");


            Role clientRole = roleRepository.save(new Role("ROLE_CLIENT"));


            System.out.println("✅ ROLE_CLIENT créé (aucune authority - pas d'accès admin)");


            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@bank.com");
            admin.setRole(adminRole);
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Utilisateur admin créé");

            User employe = new User();
            employe.setUsername("employe");
            employe.setPassword(passwordEncoder.encode("employe123"));
            employe.setEmail("employe@bank.com");
            employe.setRole(employeRole);
            employe.setEnabled(true);
            userRepository.save(employe);
            System.out.println("✅ Utilisateur employe créé");

            Client clientAhmed = new Client();
            clientAhmed.setNom("Ahmed Benali");
            clientAhmed.setEmail("ahmed@email.com");
            clientAhmed.setCreatedBy("system");
            clientAhmed.setLastModifiedBy("system");
            clientRepository.save(clientAhmed);

            User clientUser = new User();
            clientUser.setUsername("ahmed");
            clientUser.setPassword(passwordEncoder.encode("ahmed123"));
            clientUser.setEmail("ahmed@email.com");
            clientUser.setRole(clientRole);
            clientUser.setClient(clientAhmed);
            clientUser.setEnabled(true);
            userRepository.save(clientUser);
            System.out.println("✅ Utilisateur client ahmed créé");

            System.out.println("========================================");
            System.out.println("✅ Initialisation terminée !");
            System.out.println("========================================");



        };
    }
}