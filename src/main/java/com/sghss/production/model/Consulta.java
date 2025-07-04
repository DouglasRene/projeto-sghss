// src/main/java/com/sghss/production/model/Consulta.java
package com.sghss.production.model;

import com.sghss.production.model.enums.StatusConsulta;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultas") // Nome da tabela no banco de dados
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING) // Armazena o nome do enum (ex: "AGENDADA")
    @Column(nullable = false)
    private StatusConsulta status;

    @ManyToOne(fetch = FetchType.LAZY) // Muitas consultas para um paciente
    @JoinColumn(name = "paciente_id", nullable = false) // Coluna da chave estrangeira
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY) // Muitas consultas para um profissional de saúde (Usuario)
    @JoinColumn(name = "profissional_saude_id", nullable = false) // Coluna da chave estrangeira
    private Usuario profissionalSaude; // Usuário com ROLE_PROFISSIONAL_SAUDE

    @Column(columnDefinition = "TEXT") // Pode armazenar textos longos
    private String observacoes;

    // Construtor sem ID para uso no serviço ao criar uma nova consulta
    public Consulta(LocalDateTime dataHora, StatusConsulta status, Paciente paciente, Usuario profissionalSaude, String observacoes) {
        this.dataHora = dataHora;
        this.status = status;
        this.paciente = paciente;
        this.profissionalSaude = profissionalSaude;
        this.observacoes = observacoes;
    }
}