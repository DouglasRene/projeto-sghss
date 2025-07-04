package com.sghss.production.dto.auth;

import lombok.AllArgsConstructor; // << Adicione esta importação
import lombok.Data;
import lombok.NoArgsConstructor; // << Se você quiser um construtor sem argumentos também

@Data // Gera getters, setters, toString, equals, hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // << Gera um construtor com todos os campos
public class AuthResponseDTO {
    private String username;
    private String token;
    private String profile; // Ou perfis, dependendo da sua necessidade
}