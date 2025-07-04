// src/main/java/com/sghss/production/controller/HistoricoMedicoController.java
package com.sghss.production.controller;

import com.sghss.production.dto.historico.HistoricoMedicoRequestDTO;
import com.sghss.production.dto.historico.HistoricoMedicoResponseDTO;
import com.sghss.production.model.Usuario;
import com.sghss.production.model.Perfil; // Novo import
import com.sghss.production.service.HistoricoMedicoService;
import com.sghss.production.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Novo import
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historico-medico")
@Tag(name = "Histórico Médico", description = "Endpoints para gerenciamento do histórico médico dos pacientes.")
public class HistoricoMedicoController {

    private final HistoricoMedicoService historicoMedicoService;
    private final UserService userService;

    public HistoricoMedicoController(HistoricoMedicoService historicoMedicoService, UserService userService) {
        this.historicoMedicoService = historicoMedicoService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE')")
    @Operation(summary = "Adicionar uma nova entrada ao histórico médico",
               description = "Registra uma nova informação no histórico médico de um paciente. Requer ID do paciente, tipo de entrada e descrição. Acesso para ADMIN e PROFISSIONAL_SAUDE.")
    public ResponseEntity<HistoricoMedicoResponseDTO> adicionarEntradaHistorico(
            @Valid @RequestBody HistoricoMedicoRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long registradoPorUsuarioId;

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Usuário não autenticado ou contexto de segurança inválido.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String usernameLogado = userDetails.getUsername();

        Usuario usuarioLogado = userService.findByUsername(usernameLogado)
                                    .orElseThrow(ChangeSetPersister.NotFoundException::new);
        registradoPorUsuarioId = usuarioLogado.getId();

        HistoricoMedicoResponseDTO novaEntrada = historicoMedicoService.adicionarEntradaHistorico(requestDTO, registradoPorUsuarioId);
        return new ResponseEntity<>(novaEntrada, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')")
    @Operation(summary = "Buscar entrada do histórico por ID",
               description = "Retorna os detalhes de uma entrada específica no histórico médico pelo seu ID. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<HistoricoMedicoResponseDTO> buscarEntradaHistoricoPorId(@PathVariable Long id)
            throws ChangeSetPersister.NotFoundException {

        HistoricoMedicoResponseDTO entrada = historicoMedicoService.buscarEntradaHistoricoPorId(id);

        // Lógica de segurança para PACIENTES
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Verifica se o usuário logado possui a ROLE_PACIENTE
        boolean isPaciente = authentication.getAuthorities().stream()
                               .anyMatch(a -> a.getAuthority().equals(Perfil.ROLE_PACIENTE.getAuthority()));

        if (isPaciente) {
            Long pacienteIdDoUsuarioLogado = userService.getIdDoPacienteLogado(); // Pega o ID do paciente associado ao usuário logado

            // Se o ID do paciente da entrada não for o mesmo do paciente logado, negue o acesso
            if (!entrada.getPacienteId().equals(pacienteIdDoUsuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para visualizar o histórico de outro paciente.");
            }
        }
        return ResponseEntity.ok(entrada);
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')")
    @Operation(summary = "Listar histórico por ID do paciente",
               description = "Retorna todas as entradas do histórico médico de um paciente específico. Acesso para ADMIN, PROFISSIONAL_SAUDE e PACIENTE.")
    public ResponseEntity<List<HistoricoMedicoResponseDTO>> listarHistoricoPorPaciente(@PathVariable Long pacienteId)
            throws ChangeSetPersister.NotFoundException {

        // Lógica de segurança para PACIENTES
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isPaciente = authentication.getAuthorities().stream()
                               .anyMatch(a -> a.getAuthority().equals(Perfil.ROLE_PACIENTE.getAuthority()));

        if (isPaciente) {
            Long pacienteIdDoUsuarioLogado = userService.getIdDoPacienteLogado(); // Pega o ID do paciente associado ao usuário logado

            // Se o pacienteId na URL não for o mesmo do paciente logado, negue o acesso
            if (!pacienteId.equals(pacienteIdDoUsuarioLogado)) {
                throw new AccessDeniedException("Você não tem permissão para visualizar o histórico de outro paciente.");
            }
        }

        List<HistoricoMedicoResponseDTO> historico = historicoMedicoService.listarHistoricoPorPaciente(pacienteId);
        return ResponseEntity.ok(historico);
    }

    // TODO: Adicionar endpoints para atualizar e deletar, se os métodos forem implementados no service e permitidos pelas regras de negócio
    // @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<HistoricoMedicoResponseDTO> atualizarEntrada(@PathVariable Long id, @Valid @RequestBody HistoricoMedicoRequestDTO requestDTO) { ... }

    // @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    // public ResponseEntity<Void> deletarEntrada(@PathVariable Long id) { ... }
}