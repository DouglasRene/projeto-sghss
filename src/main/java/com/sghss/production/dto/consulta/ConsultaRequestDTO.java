// src/main/java/com/sghss/production/dto/consulta/ConsultaRequestDTO.java
package com.sghss.production.dto.consulta;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório.")
    private Long pacienteId;

    @NotNull(message = "O ID do profissional de saúde é obrigatório.")
    private Long profissionalSaudeId;

    @NotNull(message = "A data e hora da consulta são obrigatórias.")
    @FutureOrPresent(message = "A data e hora da consulta devem ser no presente ou futuro.")
    private LocalDateTime dataHora;

    private String observacoes;
}