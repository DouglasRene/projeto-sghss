// src/main/java/com/sghss/production/model/ExamePaciente.java
package com.sghss.production.model;

import com.sghss.production.model.enums.StatusExame; // Importe o enum
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "exames_pacientes") // Nome da tabela no banco de dados
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamePaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Muitos exames para um paciente
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY) // Muitos exames de paciente para um tipo de exame
    @JoinColumn(name = "tipo_exame_id", nullable = false)
    private TipoExame tipoExame;

    @Column(nullable = false)
    private LocalDateTime dataRealizacao; // Quando o exame foi coletado ou realizado

    private LocalDateTime dataResultado; // Quando o resultado foi liberado (pode ser nulo inicialmente)

    @Column(length = 150)
    private String laboratorio; // Nome do laboratório onde o exame foi realizado

    @Column(columnDefinition = "TEXT") // Resultados brutos ou sumarizados (ex: JSON de resultados, ou texto)
    private String resultados;

    @Column(columnDefinition = "TEXT")
    private String observacoes; // Observações adicionais sobre o exame/resultado

    @Enumerated(EnumType.STRING) // Armazena o nome do enum (ex: "SOLICITADO")
    @Column(nullable = false)
    private StatusExame status;

    @ManyToOne(fetch = FetchType.LAZY) // Quem solicitou o exame (profissional de saúde)
    @JoinColumn(name = "solicitado_por_usuario_id", nullable = false)
    private Usuario solicitadoPor;

    @ManyToOne(fetch = FetchType.LAZY) // Opcional: Relaciona a uma consulta específica
    @JoinColumn(name = "consulta_id") // Pode ser nulo
    private Consulta consulta;

    @Column(length = 500) // URL para o documento do resultado (PDF, imagem, etc.)
    private String documentoUrl;

    // Construtor sem ID para uso no serviço ao criar uma nova entrada
    public ExamePaciente(Paciente paciente, TipoExame tipoExame, LocalDateTime dataRealizacao,
                         LocalDateTime dataResultado, String laboratorio, String resultados,
                         String observacoes, StatusExame status, Usuario solicitadoPor,
                         Consulta consulta, String documentoUrl) {
        this.paciente = paciente;
        this.tipoExame = tipoExame;
        this.dataRealizacao = dataRealizacao;
        this.dataResultado = dataResultado;
        this.laboratorio = laboratorio;
        this.resultados = resultados;
        this.observacoes = observacoes;
        this.status = status;
        this.solicitadoPor = solicitadoPor;
        this.consulta = consulta;
        this.documentoUrl = documentoUrl;
    }
}