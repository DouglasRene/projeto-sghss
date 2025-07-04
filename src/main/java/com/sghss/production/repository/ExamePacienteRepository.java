// src/main/java/com/sghss/production/repository/ExamePacienteRepository.java
package com.sghss.production.repository;

import com.sghss.production.model.ExamePaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamePacienteRepository extends JpaRepository<ExamePaciente, Long> {

    // Método para buscar todos os exames de um paciente específico
    List<ExamePaciente> findByPacienteId(Long pacienteId);

    // Método para buscar exames de um paciente por tipo de exame
    List<ExamePaciente> findByPacienteIdAndTipoExameNomeContainingIgnoreCase(Long pacienteId, String tipoExameNome);
}