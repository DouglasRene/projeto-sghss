// src/main/java/com/sghss/production/dto/historico/HistoricoMedicoResponseDTO.java
package com.sghss.production.dto.historico;

import com.sghss.production.model.enums.TipoEntradaHistorico;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMedicoResponseDTO {

    private Long id;
    private LocalDateTime dataRegistro;
    private TipoEntradaHistorico tipoEntrada;
    private String descricao;

    // Informações resumidas do paciente
    private Long pacienteId;
    private String pacienteNome;
    private String pacienteCpf;

    // Informações resumidas do usuário que registrou
    private Long registradoPorId;
    private String registradoPorUsername;

    // Informações da consulta associada (se houver)
    private Long consultaId;
    private LocalDateTime consultaDataHora; // Opcional, para dar mais contexto
}