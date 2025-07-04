// src/main/java/com/sghss/production/dto/consulta/ConsultaResponseDTO.java
package com.sghss.production.dto.consulta;

import com.sghss.production.model.enums.StatusConsulta;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaResponseDTO {

    private Long id;
    private LocalDateTime dataHora;
    private StatusConsulta status;

    // Informações resumidas do paciente
    private Long pacienteId;
    private String pacienteNome;
    private String pacienteCpf;

    // Informações resumidas do profissional de saúde
    private Long profissionalSaudeId;
    private String profissionalSaudeNome; // Username do profissional (geralmente o email)
}