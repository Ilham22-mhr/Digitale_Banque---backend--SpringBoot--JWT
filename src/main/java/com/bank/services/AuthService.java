package com.bank.services;

import com.bank.dtos.AuthResponseDTO;
import com.bank.dtos.LoginDTO;
import com.bank.entities.User;
import com.bank.repositories.UserRepository;
import com.bank.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponseDTO login(LoginDTO loginDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.getUsername(),
                        loginDTO.getPassword()
                )
        );


        User user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        if (user.getRole() != null && !authorities.contains(user.getRole().getName())) {
            authorities.add(user.getRole().getName());
        }


        System.out.println(" Authentification de: " + user.getUsername());
        System.out.println("Authorities récupérées: " + authorities);
        System.out.println("Rôle: " + (user.getRole() != null ? user.getRole().getName() : "Aucun"));


        String token = jwtUtil.generateToken(user.getUsername(), authorities);


        AuthResponseDTO response = new AuthResponseDTO();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole() != null ? user.getRole().getName() : "ROLE_CLIENT");

        if (user.getClient() != null) {
            response.setClientId(user.getClient().getId());
        }

        return response;
    }
}