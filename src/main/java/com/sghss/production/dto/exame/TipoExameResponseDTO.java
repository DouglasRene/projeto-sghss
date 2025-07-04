// src/main/java/com/sghss/production/dto/exame/TipoExameResponseDTO.java
package com.sghss.production.dto.exame;

import lombok.Data;

@Data
public class TipoExameResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String unidadeMedidaPadrao;
    private String valoresReferencia;
}