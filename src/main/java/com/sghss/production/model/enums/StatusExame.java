// src/main/java/com/sghss/production/model/enums/StatusExame.java
package com.sghss.production.model.enums;

public enum StatusExame {
    SOLICITADO,         // Exame solicitado pelo profissional de saúde
    EM_ANDAMENTO,       // Exame coletado e sendo processado
    CONCLUIDO,          // Resultados do exame estão disponíveis
    CANCELADO           // Exame foi cancelado por algum motivo
}