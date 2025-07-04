// src/main/java/com/sghss/production/model/Paciente.java
package com.sghss.production.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(unique = true, nullable = false)
    private String cpf; // CPF como identificador único

    private LocalDate dataNascimento;

    @Column(nullable = true) // Pode ser nulo, se não for obrigatório
    private String telefone;

    @Column(nullable = true)
    private String endereco;

}