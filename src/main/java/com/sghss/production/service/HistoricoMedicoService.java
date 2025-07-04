// src/main/java/com/sghss/production/service/HistoricoMedicoService.java
package com.sghss.production.service;

import com.sghss.production.dto.historico.HistoricoMedicoRequestDTO;
import com.sghss.production.dto.historico.HistoricoMedicoResponseDTO;
import com.sghss.production.model.Consulta;
import com.sghss.production.model.HistoricoMedico;
import com.sghss.production.model.Paciente;
import com.sghss.production.model.Usuario;
import com.sghss.production.repository.ConsultaRepository;
import com.sghss.production.repository.HistoricoMedicoRepository;
import com.sghss.production.repository.PacienteRepository;
import com.sghss.production.repository.UsuarioRepository;
import org.springframework.data.crossstore.ChangeSetPersister; // Para NotFoundException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricoMedicoService {

    private final HistoricoMedicoRepository historicoMedicoRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConsultaRepository consultaRepository; // Injetar ConsultaRepository

    public HistoricoMedicoService(HistoricoMedicoRepository historicoMedicoRepository,
                                  PacienteRepository pacienteRepository,
                                  UsuarioRepository usuarioRepository,
                                  ConsultaRepository consultaRepository) {
        this.historicoMedicoRepository = historicoMedicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public HistoricoMedicoResponseDTO adicionarEntradaHistorico(HistoricoMedicoRequestDTO requestDTO, Long registradoPorUsuarioId)
            throws ChangeSetPersister.NotFoundException {

        // 1. Buscar Paciente
        Paciente paciente = pacienteRepository.findById(requestDTO.getPacienteId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // 2. Buscar Usuário que está registrando (profissional de saúde/admin)
        Usuario registradoPor = usuarioRepository.findById(registradoPorUsuarioId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // 3. Buscar Consulta (se fornecida)
        Consulta consulta = null;
        if (requestDTO.getConsultaId() != null) {
            consulta = consultaRepository.findById(requestDTO.getConsultaId())
                    .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
            // TODO: Opcional - Validar se a consulta pertence ao paciente ou profissional correto
        }

        // 4. Criar a entidade HistoricoMedico
        HistoricoMedico novaEntrada = new HistoricoMedico(
                LocalDateTime.now(), // Data e hora do registro
                requestDTO.getTipoEntrada(),
                requestDTO.getDescricao(),
                paciente,
                registradoPor,
                consulta
        );

        // 5. Salvar e mapear para DTO de resposta
        HistoricoMedico savedEntrada = historicoMedicoRepository.save(novaEntrada);
        return mapToResponseDTO(savedEntrada);
    }

    @Transactional(readOnly = true)
    public HistoricoMedicoResponseDTO buscarEntradaHistoricoPorId(Long id) throws ChangeSetPersister.NotFoundException {
        HistoricoMedico entrada = historicoMedicoRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return mapToResponseDTO(entrada);
    }

    @Transactional(readOnly = true)
    public List<HistoricoMedicoResponseDTO> listarHistoricoPorPaciente(Long pacienteId) throws ChangeSetPersister.NotFoundException {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return historicoMedicoRepository.findByPacienteOrderByDataRegistroDesc(paciente).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // TODO: Adicionar método para atualizar entrada de histórico (se permitido pelas regras de negócio)
    // @Transactional
    // public HistoricoMedicoResponseDTO atualizarEntradaHistorico(Long id, HistoricoMedicoRequestDTO requestDTO) { ... }

    // TODO: Adicionar método para deletar entrada de histórico (se permitido e com auditoria)
    // @Transactional
    // public void deletarEntradaHistorico(Long id) { ... }


    // Método auxiliar para mapear Entidade HistoricoMedico para HistoricoMedicoResponseDTO
    private HistoricoMedicoResponseDTO mapToResponseDTO(HistoricoMedico historico) {
        HistoricoMedicoResponseDTO responseDTO = new HistoricoMedicoResponseDTO();
        responseDTO.setId(historico.getId());
        responseDTO.setDataRegistro(historico.getDataRegistro());
        responseDTO.setTipoEntrada(historico.getTipoEntrada());
        responseDTO.setDescricao(historico.getDescricao());

        // Mapear dados do paciente
        if (historico.getPaciente() != null) {
            responseDTO.setPacienteId(historico.getPaciente().getId());
            responseDTO.setPacienteNome(historico.getPaciente().getNomeCompleto());
            responseDTO.setPacienteCpf(historico.getPaciente().getCpf());
        }

        // Mapear dados do usuário que registrou
        if (historico.getRegistradoPor() != null) {
            responseDTO.setRegistradoPorId(historico.getRegistradoPor().getId());
            responseDTO.setRegistradoPorUsername(historico.getRegistradoPor().getUsername());
        }

        // Mapear dados da consulta associada (se houver)
        if (historico.getConsulta() != null) {
            responseDTO.setConsultaId(historico.getConsulta().getId());
            responseDTO.setConsultaDataHora(historico.getConsulta().getDataHora());
        }
        return responseDTO;
    }
}