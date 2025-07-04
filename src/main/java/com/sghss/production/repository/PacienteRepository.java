// src/main/java/com/sghss/production/repository/PacienteRepository.java
package com.sghss.production.repository;

import com.sghss.production.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    Optional<Paciente> findByCpf(String cpf); // Método para buscar paciente por CPF
}