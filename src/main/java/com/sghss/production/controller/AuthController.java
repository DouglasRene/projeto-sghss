package com.sghss.production.controller;

import com.sghss.production.dto.auth.AuthResponseDTO;
import com.sghss.production.dto.auth.LoginRequestDTO;
import com.sghss.production.dto.user.UserRequestDTO;
import com.sghss.production.dto.user.UserResponseDTO;
import com.sghss.production.service.AuthService;
import com.sghss.production.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação e Usuários", description = "Endpoints para login e gerenciamento de usuários.")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário e obter JWT",
               description = "Autentica um usuário com username e senha e retorna um JSON Web Token (JWT).")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        AuthResponseDTO response = authService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register") // Endpoint para registro (inicialmente para ADMIN criar usuários)
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode registrar novos usuários
    @Operation(summary = "Registrar novo usuário",
               description = "Registra um novo usuário no sistema. Apenas usuários com perfil ADMIN podem realizar esta operação.")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody @Valid UserRequestDTO request) {
        UserResponseDTO newUser = userService.createUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Exemplo de como consultar um usuário
    @Operation(summary = "Consultar dados de um usuário",
               description = "Consulta os dados de um usuário pelo ID. Acesso restrito por perfil.")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }
}