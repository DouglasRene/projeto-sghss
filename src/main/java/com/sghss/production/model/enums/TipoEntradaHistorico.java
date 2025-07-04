// src/main/java/com/sghss/production/model/enums/TipoEntradaHistorico.java
package com.sghss.production.model.enums;

public enum TipoEntradaHistorico {
    DIAGNOSTICO,        // Registro de um diagnóstico médico
    TRATAMENTO,         // Detalhes sobre um tratamento aplicado
    MEDICAMENTO,        // Prescrição ou administração de medicamentos
    EXAME,              // Resultados de exames laboratoriais ou de imagem
    ALERGIA,            // Informações sobre alergias conhecidas
    VACINA,             // Registro de vacinas administradas
    OBSERVACAO_GERAL,   // Observações gerais ou anotações clínicas
    PROCEDIMENTO,       // Registro de procedimentos cirúrgicos ou não-cirúrgicos
    HISTORICO_FAMILIAR  // Informações sobre histórico de saúde familiar
}