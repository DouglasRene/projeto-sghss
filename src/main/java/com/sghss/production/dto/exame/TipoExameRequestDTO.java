// src/main/java/com/sghss/production/dto/exame/TipoExameRequestDTO.java
package com.sghss.production.dto.exame;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TipoExameRequestDTO {

    @NotBlank(message = "O nome do tipo de exame é obrigatório.")
    @Size(max = 100, message = "O nome do tipo de exame deve ter no máximo 100 caracteres.")
    private String nome;

    @Size(max = 500, message = "A descrição do tipo de exame deve ter no máximo 500 caracteres.")
    private String descricao;

    @Size(max = 50, message = "A unidade de medida padrão deve ter no máximo 50 caracteres.")
    private String unidadeMedidaPadrao;

    @Size(max = 500, message = "Os valores de referência devem ter no máximo 500 caracteres.")
    private String valoresReferencia;
}