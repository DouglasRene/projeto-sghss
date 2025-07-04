// src/main/java/com/sghss/production/controller/PacienteController.java
package com.sghss.production.controller;

import com.sghss.production.dto.paciente.PacienteRequestDTO;
import com.sghss.production.dto.paciente.PacienteResponseDTO;
import com.sghss.production.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFISSIONAL_SAUDE')")
    public ResponseEntity<PacienteResponseDTO> criarPaciente(@Valid @RequestBody PacienteRequestDTO requestDTO) {
        // Remover try-catch para IllegalArgumentException
        PacienteResponseDTO novoPaciente = pacienteService.criarPaciente(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPaciente);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')")
    public ResponseEntity<List<PacienteResponseDTO>> listarPacientes() {
        List<PacienteResponseDTO> pacientes = pacienteService.listarTodosPacientes();
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorId(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        // Remover try-catch
        PacienteResponseDTO paciente = pacienteService.buscarPacientePorId(id);
        return ResponseEntity.ok(paciente);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PROFISSIONAL_SAUDE')")
    public ResponseEntity<PacienteResponseDTO> atualizarPaciente(@PathVariable Long id, @Valid @RequestBody PacienteRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        // Remover try-catch para IllegalArgumentException
        PacienteResponseDTO pacienteAtualizado = pacienteService.atualizarPaciente(id, requestDTO);
        return ResponseEntity.ok(pacienteAtualizado);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarPaciente(@PathVariable Long id) throws ChangeSetPersister.NotFoundException {
        // Remover try-catch
        pacienteService.deletarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}