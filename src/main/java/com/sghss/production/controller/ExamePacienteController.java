// src/main/java/com/sghss/production/controller/ExamePacienteController.java
package com.sghss.production.controller;

import com.sghss.production.dto.exame.ExamePacienteRequestDTO;
import com.sghss.production.dto.exame.ExamePacienteResponseDTO;
import com.sghss.production.model.Perfil; // Importe o enum Perfil
import com.sghss.production.model.Usuario; // Importe a entidade Usuario
import com.sghss.production.service.ExamePacienteService;
import com.sghss.production.service.UserService; // Para obter o ID do usuário logado

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Para permissão negada
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames-pacientes")
@Tag(name = "Exames do Paciente", description = "Endpoints para gerenciamento dos exames laboratoriais realizados pelos pacientes.")
public class ExamePacienteController {

    private final ExamePacienteService examePacienteService;
    private final UserService userService; // Usado para obter o ID do usuário logado e o ID do paciente logado

    public ExamePacienteController(ExamePacienteService examePacienteService, UserService userService) {
        this.examePacienteService = examePacienteService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')") // ADMIN ou PROFISSIONAL_SAUDE podem registrar exames
    @Operation(summary = "Registrar um novo exame para um paciente",
               description = "Registra os detalhes de um exame laboratorial realizado por um paciente.")
    public ResponseEntity<ExamePacienteResponseDTO> registrarExamePaciente(
            @Valid @RequestBody ExamePacienteRequestDTO requestDTO) throws NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long registradoPorUsuarioId = null;

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Usuário não autenticado ou contexto de segurança inválido.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String usernameLogado = userDetails.getUsername();

        // Obtém o ID da entidade Usuario do usuário logado para associar como solicitante
        Usuario usuarioLogado = userService.findByUsername(usernameLogado)
                                    .orElseThrow(NotFoundException::new);
        registradoPorUsuarioId = usuarioLogado.getId();


        ExamePacienteResponseDTO novoExame = examePacienteService.registrarExamePaciente(requestDTO, registradoPorUsuarioId);
        return new ResponseEntity<>(novoExame, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Todos podem buscar, mas PACIENTE tem restrição
    @Operation(summary = "Buscar exame do paciente por ID",
               description = "Retorna os detalhes de um exame específico do paciente pelo seu ID. Pacientes só podem ver os próprios.")
    public ResponseEntity<ExamePacienteResponseDTO> buscarExamePacientePorId(@PathVariable Long id)
            throws NotFoundException {

        ExamePacienteResponseDTO exame = examePacienteService.buscarExamePacientePorId(id);

        // Lógica de segurança para PACIENTES
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = authentication.getAuthorities().stream()
                               .anyMatch(a -> a.getAuthority().equals(Perfil.ROLE_PACIENTE.getAuthority()));

        if (isPaciente) {
            Long pacienteIdDoUsuarioLogado = userService.getIdDoPacienteLogado(); // Obtém o ID do paciente do usuário logado

            // Se o pacienteId do exame não for o mesmo do paciente logado, negue o acesso
            if (!exame.getPacienteId().equals(pacienteIdDoUsuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para visualizar este exame de outro paciente.");
            }
        }
        return ResponseEntity.ok(exame);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Todos podem listar, mas PACIENTE tem restrição
    @Operation(summary = "Listar exames por ID do paciente",
               description = "Retorna todos os exames laboratoriais de um paciente específico. Pacientes só podem ver os próprios.")
    public ResponseEntity<List<ExamePacienteResponseDTO>> listarExamesPorPaciente(@PathVariable Long pacienteId)
            throws NotFoundException {

        // Lógica de segurança para PACIENTES
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = authentication.getAuthorities().stream()
                               .anyMatch(a -> a.getAuthority().equals(Perfil.ROLE_PACIENTE.getAuthority()));

        if (isPaciente) {
            Long pacienteIdDoUsuarioLogado = userService.getIdDoPacienteLogado(); // Obtém o ID do paciente do usuário logado

            // Se o pacienteId na URL não for o mesmo do paciente logado, negue o acesso
            if (!pacienteId.equals(pacienteIdDoUsuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para visualizar os exames de outro paciente.");
            }
        }

        List<ExamePacienteResponseDTO> exames = examePacienteService.listarExamesPorPaciente(pacienteId);
        return ResponseEntity.ok(exames);
    }

    @GetMapping("/paciente/{pacienteId}/tipo")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Todos podem listar por tipo, mas PACIENTE tem restrição
    @Operation(summary = "Listar exames por ID do paciente e tipo de exame",
               description = "Retorna os exames de um paciente filtrados pelo nome do tipo de exame. Pacientes só podem ver os próprios.")
    public ResponseEntity<List<ExamePacienteResponseDTO>> buscarExamesPorPacienteETipo(
            @PathVariable Long pacienteId,
            @RequestParam String tipoExameNome) throws NotFoundException {

        // Lógica de segurança para PACIENTES
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = authentication.getAuthorities().stream()
                               .anyMatch(a -> a.getAuthority().equals(Perfil.ROLE_PACIENTE.getAuthority()));

        if (isPaciente) {
            Long pacienteIdDoUsuarioLogado = userService.getIdDoPacienteLogado();

            if (!pacienteId.equals(pacienteIdDoUsuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para visualizar os exames de outro paciente.");
            }
        }

        List<ExamePacienteResponseDTO> exames = examePacienteService.buscarExamesPorPacienteETipo(pacienteId, tipoExameNome);
        return ResponseEntity.ok(exames);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')") // ADMIN ou PROFISSIONAL_SAUDE podem atualizar exames
    @Operation(summary = "Atualizar um exame de paciente",
               description = "Atualiza os detalhes de um exame laboratorial existente para um paciente.")
    public ResponseEntity<ExamePacienteResponseDTO> atualizarExamePaciente(
            @PathVariable Long id,
            @Valid @RequestBody ExamePacienteRequestDTO requestDTO) throws NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long atualizadoPorUsuarioId = null;

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Usuário não autenticado ou contexto de segurança inválido.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String usernameLogado = userDetails.getUsername();

        Usuario usuarioLogado = userService.findByUsername(usernameLogado)
                                    .orElseThrow(NotFoundException::new);
        atualizadoPorUsuarioId = usuarioLogado.getId();

        ExamePacienteResponseDTO updatedExame = examePacienteService.atualizarExamePaciente(id, requestDTO, atualizadoPorUsuarioId);
        return ResponseEntity.ok(updatedExame);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode deletar exames (sensível)
    @Operation(summary = "Deletar um exame de paciente",
               description = "Exclui um exame laboratorial do sistema pelo seu ID.")
    public ResponseEntity<Void> deletarExamePaciente(@PathVariable Long id) throws NotFoundException {
        examePacienteService.deletarExamePaciente(id);
        return ResponseEntity.noContent().build();
    }
}