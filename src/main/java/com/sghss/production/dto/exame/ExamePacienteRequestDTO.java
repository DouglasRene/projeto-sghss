// src/main/java/com/sghss/production/dto/exame/ExamePacienteRequestDTO.java
package com.sghss.production.dto.exame;

import com.sghss.production.model.enums.StatusExame; // Importe o enum
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamePacienteRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório.")
    private Long pacienteId;

    @NotNull(message = "O ID do tipo de exame é obrigatório.")
    private Long tipoExameId;

    @NotNull(message = "A data de realização do exame é obrigatória.")
    private LocalDateTime dataRealizacao;

    private LocalDateTime dataResultado; // Pode ser nulo no início

    @Size(max = 150, message = "O nome do laboratório deve ter no máximo 150 caracteres.")
    private String laboratorio;

    @Size(max = 2000, message = "Os resultados do exame devem ter no máximo 2000 caracteres.")
    private String resultados; // Pode ser um JSON string ou texto simples

    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
    private String observacoes;

    @NotNull(message = "O status do exame é obrigatório.")
    private StatusExame status;

    // O ID do usuário que solicitou virá do contexto de segurança, não do DTO.
    // private Long solicitadoPorUsuarioId; // REMOVIDO para segurança

    private Long consultaId; // Opcional, se o exame for associado a uma consulta

    @Size(max = 500, message = "A URL do documento deve ter no máximo 500 caracteres.")
    private String documentoUrl;
}