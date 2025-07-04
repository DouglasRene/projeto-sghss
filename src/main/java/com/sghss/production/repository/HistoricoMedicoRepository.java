// src/main/java/com/sghss/production/repository/HistoricoMedicoRepository.java
package com.sghss.production.repository;

import com.sghss.production.model.Consulta;
import com.sghss.production.model.HistoricoMedico;
import com.sghss.production.model.Paciente;
import com.sghss.production.model.Usuario;
import com.sghss.production.model.enums.TipoEntradaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoMedicoRepository extends JpaRepository<HistoricoMedico, Long> {

    // Buscar todo o histórico de um paciente específico, ordenado por data de registro
    List<HistoricoMedico> findByPacienteOrderByDataRegistroDesc(Paciente paciente);

    // Buscar histórico por paciente e tipo de entrada
    List<HistoricoMedico> findByPacienteAndTipoEntradaOrderByDataRegistroDesc(Paciente paciente, TipoEntradaHistorico tipoEntrada);

    // Buscar histórico por profissional que registrou
    List<HistoricoMedico> findByRegistradoPorOrderByDataRegistroDesc(Usuario registradoPor);

    // Buscar histórico em um intervalo de datas para um paciente
    List<HistoricoMedico> findByPacienteAndDataRegistroBetweenOrderByDataRegistroDesc(Paciente paciente, LocalDateTime startDateTime, LocalDateTime endDateTime);

    // Buscar histórico associado a uma consulta específica
    List<HistoricoMedico> findByConsulta(Consulta consulta);
}