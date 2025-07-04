// src/main/java/com/sghss/production/dto/historico/HistoricoMedicoRequestDTO.java
package com.sghss.production.dto.historico;

import com.sghss.production.model.enums.TipoEntradaHistorico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMedicoRequestDTO {

    @NotNull(message = "O ID do paciente é obrigatório.")
    private Long pacienteId;

    @NotNull(message = "O tipo de entrada do histórico é obrigatório.")
    private TipoEntradaHistorico tipoEntrada;

    @NotBlank(message = "A descrição da entrada do histórico não pode estar vazia.")
    private String descricao;

    // O ID do usuário que está registrando a entrada será obtido do contexto de segurança (usuário logado)
    // private Long registradoPorId; // Removido, pois será obtido do contexto de segurança

    // Opcional: Para associar a entrada a uma consulta específica
    private Long consultaId; // Pode ser nulo
}