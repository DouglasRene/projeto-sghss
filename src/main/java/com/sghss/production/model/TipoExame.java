// src/main/java/com/sghss/production/model/TipoExame.java
package com.sghss.production.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "tipos_exames") // Nome da tabela no banco de dados
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoExame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100) // Nome do tipo de exame (ex: "Hemograma Completo")
    private String nome;

    @Column(columnDefinition = "TEXT") // Breve descrição do exame
    private String descricao;

    @Column(length = 50) // Unidade de medida padrão para resultados numéricos (ex: "mg/dL", "UI/L")
    private String unidadeMedidaPadrao;

    @Column(columnDefinition = "TEXT") // Valores de referência padrão (ex: "70-100 mg/dL")
    private String valoresReferencia;

    // Construtor sem ID para uso no serviço ao criar um novo TipoExame
    public TipoExame(String nome, String descricao, String unidadeMedidaPadrao, String valoresReferencia) {
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedidaPadrao = unidadeMedidaPadrao;
        this.valoresReferencia = valoresReferencia;
    }
}