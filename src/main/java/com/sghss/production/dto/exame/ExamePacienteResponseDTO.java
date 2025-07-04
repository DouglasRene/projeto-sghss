// src/main/java/com/sghss/production/dto/exame/ExamePacienteResponseDTO.java
package com.sghss.production.dto.exame;

import com.sghss.production.model.enums.StatusExame; // Importe o enum
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExamePacienteResponseDTO {
    private Long id;
    private Long pacienteId;
    private String pacienteNome; // Para facilitar a visualização
    private Long tipoExameId;
    private String tipoExameNome; // Para facilitar a visualização
    private LocalDateTime dataRealizacao;
    private LocalDateTime dataResultado;
    private String laboratorio;
    private String resultados;
    private String observacoes;
    private StatusExame status;
    private Long solicitadoPorUsuarioId;
    private String solicitadoPorUsername; // Para facilitar a visualização
    private Long consultaId;
    private LocalDateTime consultaDataHora; // Para facilitar a visualização da consulta associada
    private String documentoUrl;
}