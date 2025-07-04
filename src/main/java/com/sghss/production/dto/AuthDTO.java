package com.sghss.production.dto;

import com.sghss.production.model.Perfil;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDTO {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RegisterRequest {
        @NotBlank(message = "O nome é obrigatório")
        private String nome;

        @Email(message = "Email inválido")
        @NotBlank(message = "O email é obrigatório")
        private String email;

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        private String senha;

        @NotNull(message = "O perfil é obrigatório")
        private Perfil perfil;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        @Email(message = "Email inválido")
        @NotBlank(message = "O email é obrigatório")
        private String email;

        @NotBlank(message = "A senha é obrigatória")
        private String senha;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthenticationResponse {
        private String token;
    }
}
