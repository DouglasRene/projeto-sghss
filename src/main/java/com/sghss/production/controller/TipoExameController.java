// src/main/java/com/sghss/production/controller/TipoExameController.java
package com.sghss.production.controller;

import com.sghss.production.dto.exame.TipoExameRequestDTO;
import com.sghss.production.dto.exame.TipoExameResponseDTO;
import com.sghss.production.service.TipoExameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-exames")
@Tag(name = "Tipos de Exames", description = "Endpoints para gerenciamento dos tipos de exames laboratoriais disponíveis.")
public class TipoExameController {

    private final TipoExameService tipoExameService;

    public TipoExameController(TipoExameService tipoExameService) {
        this.tipoExameService = tipoExameService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode criar novos tipos de exames
    @Operation(summary = "Criar um novo tipo de exame",
               description = "Registra um novo tipo de exame laboratorial no sistema.")
    public ResponseEntity<TipoExameResponseDTO> criarTipoExame(
            @Valid @RequestBody TipoExameRequestDTO requestDTO) {
        TipoExameResponseDTO novoTipo = tipoExameService.criarTipoExame(requestDTO);
        return new ResponseEntity<>(novoTipo, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Todos podem buscar o tipo de exame para ver detalhes
    @Operation(summary = "Buscar tipo de exame por ID",
               description = "Retorna os detalhes de um tipo de exame específico pelo seu ID.")
    public ResponseEntity<TipoExameResponseDTO> buscarTipoExamePorId(@PathVariable Long id)
            throws NotFoundException {
        TipoExameResponseDTO tipoExame = tipoExameService.buscarTipoExamePorId(id);
        return ResponseEntity.ok(tipoExame);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFISSIONAL_SAUDE', 'PACIENTE')") // Todos podem listar os tipos de exames disponíveis
    @Operation(summary = "Listar todos os tipos de exames",
               description = "Retorna uma lista de todos os tipos de exames laboratoriais cadastrados.")
    public ResponseEntity<List<TipoExameResponseDTO>> listarTodosTiposExames() {
        List<TipoExameResponseDTO> tiposExames = tipoExameService.listarTodosTiposExames();
        return ResponseEntity.ok(tiposExames);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode atualizar tipos de exames
    @Operation(summary = "Atualizar um tipo de exame existente",
               description = "Atualiza as informações de um tipo de exame pelo seu ID.")
    public ResponseEntity<TipoExameResponseDTO> atualizarTipoExame(
            @PathVariable Long id,
            @Valid @RequestBody TipoExameRequestDTO requestDTO) throws NotFoundException {
        TipoExameResponseDTO updatedTipo = tipoExameService.atualizarTipoExame(id, requestDTO);
        return ResponseEntity.ok(updatedTipo);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Apenas ADMIN pode deletar tipos de exames
    @Operation(summary = "Deletar um tipo de exame",
               description = "Exclui um tipo de exame do sistema pelo seu ID.")
    public ResponseEntity<Void> deletarTipoExame(@PathVariable Long id) throws NotFoundException {
        tipoExameService.deletarTipoExame(id);
        return ResponseEntity.noContent().build();
    }
}