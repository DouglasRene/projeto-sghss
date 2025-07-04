package com.sghss.production.service;

import com.sghss.production.dto.auth.AuthResponseDTO;
import com.sghss.production.dto.auth.LoginRequestDTO; // << Mudei de AuthRequestDTO para LoginRequestDTO
import com.sghss.production.model.Usuario; // Certifique-se que esta importação está correta
import com.sghss.production.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO authenticate(LoginRequestDTO authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // A linha abaixo foi ajustada:
        String jwtToken = jwtService.generateToken(userDetails); // << AQUI ESTÁ A CORREÇÃO

        // Obtenha os perfis do usuário
        String userProfile = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // Garanta a ordem correta: username, jwtToken, userProfile
        return new AuthResponseDTO(userDetails.getUsername(), jwtToken, userProfile); // << Use userDetails.getUsername() aqui também para consistência
    }
}