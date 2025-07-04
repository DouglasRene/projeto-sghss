// src/main/java/com/sghss/production/dto/paciente/PacienteResponseDTO.java
package com.sghss.production.dto.paciente;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteResponseDTO {
    private Long id;
    private String nomeCompleto;
    private String cpf;
    private LocalDate dataNascimento;
    private String telefone;
    private String endereco;
}