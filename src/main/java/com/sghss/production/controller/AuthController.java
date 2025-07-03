package com.sghss.production.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sghss.production.dto.AuthResponseDTO;
import com.sghss.production.entities.AuthRequestDTO;
import com.sghss.production.entity.Usuario;
import com.sghss.production.repositores.UsuarioRepository;
import com.sghss.production.security.JwtService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    
    @Autowired
    public AuthController(
        AuthenticationManager authManager,
        UsuarioRepository usuarioRepository,
        JwtService jwtService
    , PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO request) {
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new AuthResponseDTO());
    }
    
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario salvo = usuarioRepository.save(usuario);
        return ResponseEntity.ok(salvo);
    }
}
