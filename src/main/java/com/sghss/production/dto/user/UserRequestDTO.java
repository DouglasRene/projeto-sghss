package com.sghss.production.dto.user;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set; // Para os perfis

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "O nome de usuário (email) é obrigatório")
    @Email(message = "O nome de usuário deve ser um email válido")
    private String username;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotEmpty(message = "Pelo menos um perfil deve ser informado")
    private Set<String> perfis; // Ex: ["ROLE_ADMIN", "ROLE_PROFISSIONAL_SAUDE"]

    private boolean enabled = true; // Por padrão, o usuário é habilitado
}