// src/main/java/com/sghss/production/controller/ConsultaController.java
package com.sghss.production.controller;

import com.sghss.production.dto.consulta.ConsultaRequestDTO;
import com.sghss.production.dto.consulta.ConsultaResponseDTO;
import com.sghss.production.model.enums.StatusConsulta;
import com.sghss.production.service.ConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@Tag(name = "Consultas", description = "Endpoints para gerenciamento de consultas médicas.")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Paciente pode agendar para si, Profissional/Admin para qualquer um
    @Operation(summary = "Agendar uma nova consulta",
               description = "Permite agendar uma nova consulta para um paciente com um profissional de saúde. Requer ID do paciente, ID do profissional e data/hora. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<ConsultaResponseDTO> agendarConsulta(@Valid @RequestBody ConsultaRequestDTO requestDTO)
            throws ChangeSetPersister.NotFoundException {
        ConsultaResponseDTO novaConsulta = consultaService.agendarConsulta(requestDTO);
        return new ResponseEntity<>(novaConsulta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Admin/Profissional vê qualquer, Paciente vê a própria
    @Operation(summary = "Buscar consulta por ID",
               description = "Retorna os detalhes de uma consulta específica pelo seu ID. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<ConsultaResponseDTO> buscarConsultaPorId(@PathVariable Long id)
            throws ChangeSetPersister.NotFoundException {
        ConsultaResponseDTO consulta = consultaService.buscarConsultaPorId(id);
        return ResponseEntity.ok(consulta);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')") // Pacientes não listam todas
    @Operation(summary = "Listar todas as consultas",
               description = "Retorna uma lista de todas as consultas agendadas. Acesso restrito a ADMIN e PROFISSIONAL_SAUDE.")
    public ResponseEntity<List<ConsultaResponseDTO>> listarTodasConsultas() {
        List<ConsultaResponseDTO> consultas = consultaService.listarTodasConsultas();
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESSIONAL_SAUDE', 'PACIENTE')") // Paciente vê as próprias, Admin/Profissional vê qualquer
    @Operation(summary = "Listar consultas por ID do paciente",
               description = "Retorna as consultas agendadas para um paciente específico. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<List<ConsultaResponseDTO>> listarConsultasPorPaciente(@PathVariable Long pacienteId)
            throws ChangeSetPersister.NotFoundException {
        List<ConsultaResponseDTO> consultas = consultaService.listarConsultasPorPaciente(pacienteId);
        return ResponseEntity.ok(consultas);
    }

    @GetMapping("/profissional/{profissionalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')") // Profissional vê as próprias, Admin vê qualquer
    @Operation(summary = "Listar consultas por ID do profissional de saúde",
               description = "Retorna as consultas agendadas para um profissional de saúde específico. Acesso para ADMIN e PROFISSIONAL_SAUDE.")
    public ResponseEntity<List<ConsultaResponseDTO>> listarConsultasPorProfissional(@PathVariable Long profissionalId)
            throws ChangeSetPersister.NotFoundException {
        List<ConsultaResponseDTO> consultas = consultaService.listarConsultasPorProfissional(profissionalId);
        return ResponseEntity.ok(consultas);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')") // Apenas Admin e Profissional podem mudar status
    @Operation(summary = "Atualizar status da consulta",
               description = "Atualiza o status de uma consulta (ex: AGENDADA, CONFIRMADA, CANCELADA, REALIZADA). Acesso restrito a ADMIN e PROFISSIONAL_SAUDE.")
    public ResponseEntity<ConsultaResponseDTO> atualizarStatusConsulta(
            @PathVariable Long id,
            @RequestParam StatusConsulta status) throws ChangeSetPersister.NotFoundException {
        ConsultaResponseDTO updatedConsulta = consultaService.atualizarStatusConsulta(id, status);
        return ResponseEntity.ok(updatedConsulta);
    }

    @PatchMapping("/{id}/reagendar")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Paciente pode reagendar a própria, Admin/Profissional qualquer
    @Operation(summary = "Reagendar consulta",
               description = "Reagenda uma consulta para uma nova data e hora, atualizando o status para REAGENDADA. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<ConsultaResponseDTO> reagendarConsulta(
            @PathVariable Long id,
            @RequestParam LocalDateTime novaDataHora) throws ChangeSetPersister.NotFoundException {
        ConsultaResponseDTO reagendadaConsulta = consultaService.reagendarConsulta(id, novaDataHora);
        return ResponseEntity.ok(reagendadaConsulta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')") // Deleção geralmente restrita ao ADMIN
    @Operation(summary = "Deletar consulta por ID",
               description = "Remove uma consulta do sistema pelo seu ID. Operação sensível, restrita a ADMIN.")
    public ResponseEntity<Void> deletarConsulta(@PathVariable Long id)
            throws ChangeSetPersister.NotFoundException {
        consultaService.deletarConsulta(id);
        return ResponseEntity.noContent().build();
    }
}