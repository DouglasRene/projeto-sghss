// src/main/java/com/sghss/production/dto/paciente/PacienteRequestDTO.java
package com.sghss.production.dto.paciente;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class PacienteRequestDTO {

    @NotBlank(message = "O nome completo é obrigatório")
    private String nomeCompleto;

    @NotBlank(message = "O CPF é obrigatório")
    @Pattern(regexp = "^\\d{11}$", message = "CPF deve conter 11 dígitos numéricos") // Validação básica de CPF
    private String cpf;

    @NotNull(message = "A data de nascimento é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    private String telefone;
    private String endereco;
}