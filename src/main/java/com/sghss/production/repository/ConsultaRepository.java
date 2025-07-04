// src/main/java/com/sghss/production/repository/ConsultaRepository.java
package com.sghss.production.repository;

import com.sghss.production.model.Consulta;
import com.sghss.production.model.Paciente;
import com.sghss.production.model.Usuario;
import com.sghss.production.model.enums.StatusConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    // Método para encontrar consultas por paciente e status
    List<Consulta> findByPacienteAndStatus(Paciente paciente, StatusConsulta status);

    // Método para encontrar consultas por profissional de saúde e data/hora
    List<Consulta> findByProfissionalSaudeAndDataHoraBetween(Usuario profissionalSaude, LocalDateTime startOfDay, LocalDateTime endOfDay);

    // Método para verificar se um profissional de saúde já tem uma consulta no mesmo horário
    // útil para evitar agendamentos sobrepostos
    Optional<Consulta> findByProfissionalSaudeAndDataHora(Usuario profissionalSaude, LocalDateTime dataHora);

    // Método para encontrar consultas de um paciente específico por um período
    List<Consulta> findByPacienteAndDataHoraBetween(Paciente paciente, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Método para encontrar consultas por profissional de saúde
    List<Consulta> findByProfissionalSaude(Usuario profissionalSaude);

    // Método para encontrar consultas por paciente
    List<Consulta> findByPaciente(Paciente paciente);
}