package com.sghss.production.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private Set<String> perfis; // Perfil como String para a resposta
    private boolean enabled;
}