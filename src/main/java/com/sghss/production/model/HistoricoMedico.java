// src/main/java/com/sghss/production/model/HistoricoMedico.java
package com.sghss.production.model;

import com.sghss.production.model.enums.TipoEntradaHistorico;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historicos_medicos") // Nome da tabela no banco de dados
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataRegistro; // Quando a entrada foi registrada

    @Enumerated(EnumType.STRING) // Armazena o nome do enum (ex: "DIAGNOSTICO")
    @Column(nullable = false)
    private TipoEntradaHistorico tipoEntrada;

    @Column(columnDefinition = "TEXT", nullable = false) // Conteúdo detalhado da entrada
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY) // Muitas entradas de histórico para um paciente
    @JoinColumn(name = "paciente_id", nullable = false) // Coluna da chave estrangeira
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY) // Muitas entradas de histórico para um usuário (quem registrou)
    @JoinColumn(name = "registrado_por_usuario_id", nullable = false) // Coluna da chave estrangeira
    private Usuario registradoPor; // O usuário (profissional de saúde/admin) que fez o registro

    @ManyToOne(fetch = FetchType.LAZY) // Opcional: Relacionar a uma consulta específica
    @JoinColumn(name = "consulta_id") // Pode ser nulo
    private Consulta consulta;

    // Construtor sem ID para uso no serviço ao criar uma nova entrada de histórico
    public HistoricoMedico(LocalDateTime dataRegistro, TipoEntradaHistorico tipoEntrada, String descricao,
                           Paciente paciente, Usuario registradoPor, Consulta consulta) {
        this.dataRegistro = dataRegistro;
        this.tipoEntrada = tipoEntrada;
        this.descricao = descricao;
        this.paciente = paciente;
        this.registradoPor = registradoPor;
        this.consulta = consulta;
    }
}