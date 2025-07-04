// src/main/java/com/sghss/production/service/ConsultaService.java
package com.sghss.production.service;

import com.sghss.production.dto.consulta.ConsultaRequestDTO;
import com.sghss.production.dto.consulta.ConsultaResponseDTO;
import com.sghss.production.model.Consulta;
import com.sghss.production.model.Paciente;
import com.sghss.production.model.Usuario;
import com.sghss.production.model.enums.StatusConsulta;
import com.sghss.production.repository.ConsultaRepository;
import com.sghss.production.repository.PacienteRepository;
import com.sghss.production.repository.UsuarioRepository; // Assumindo que você tem um UsuarioRepository
import com.sghss.production.exception.DataConflictException; // Para conflitos de agendamento
import org.springframework.data.crossstore.ChangeSetPersister; // Para NotFoundException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository; // Injetar UsuarioRepository

    public ConsultaService(ConsultaRepository consultaRepository,
                           PacienteRepository pacienteRepository,
                           UsuarioRepository usuarioRepository) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public ConsultaResponseDTO agendarConsulta(ConsultaRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        // 1. Buscar Paciente
        Paciente paciente = pacienteRepository.findById(requestDTO.getPacienteId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // 2. Buscar Profissional de Saúde (Usuário)
        Usuario profissionalSaude = usuarioRepository.findById(requestDTO.getProfissionalSaudeId())
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // TODO: Opcional - Validar se o Usuario encontrado é de fato um PROFISSIONAL_SAUDE
        // if (!profissionalSaude.getPerfis().contains(Perfil.PROFISSIONAL_SAUDE)) {
        //     throw new IllegalArgumentException("O usuário selecionado não é um profissional de saúde.");
        // }

        // 3. Validar disponibilidade do profissional no horário
        if (consultaRepository.findByProfissionalSaudeAndDataHora(profissionalSaude, requestDTO.getDataHora()).isPresent()) {
            throw new DataConflictException("O profissional de saúde já tem uma consulta agendada para este horário.");
        }

        // 4. Criar a entidade Consulta
        Consulta novaConsulta = new Consulta(
                requestDTO.getDataHora(),
                StatusConsulta.AGENDADA, // Status inicial
                paciente,
                profissionalSaude,
                requestDTO.getObservacoes()
        );

        // 5. Salvar e mapear para DTO de resposta
        Consulta savedConsulta = consultaRepository.save(novaConsulta);
        return mapToResponseDTO(savedConsulta);
    }

    @Transactional(readOnly = true)
    public ConsultaResponseDTO buscarConsultaPorId(Long id) throws ChangeSetPersister.NotFoundException {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return mapToResponseDTO(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarTodasConsultas() {
        return consultaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarConsultasPorPaciente(Long pacienteId) throws ChangeSetPersister.NotFoundException {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return consultaRepository.findByPaciente(paciente).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponseDTO> listarConsultasPorProfissional(Long profissionalId) throws ChangeSetPersister.NotFoundException {
        Usuario profissionalSaude = usuarioRepository.findById(profissionalId)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());
        return consultaRepository.findByProfissionalSaude(profissionalSaude).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConsultaResponseDTO atualizarStatusConsulta(Long id, StatusConsulta novoStatus) throws ChangeSetPersister.NotFoundException {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // TODO: Adicionar lógica de validação de transição de status se necessário
        // Ex: Não permitir mudar de CANCELADA para AGENDADA diretamente, apenas para REAGENDADA

        consulta.setStatus(novoStatus);
        Consulta updatedConsulta = consultaRepository.save(consulta);
        return mapToResponseDTO(updatedConsulta);
    }

    @Transactional
    public ConsultaResponseDTO reagendarConsulta(Long id, LocalDateTime novaDataHora) throws ChangeSetPersister.NotFoundException {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // Validar se o novo horário está disponível para o mesmo profissional
        if (consultaRepository.findByProfissionalSaudeAndDataHora(consulta.getProfissionalSaude(), novaDataHora).isPresent()) {
            throw new DataConflictException("O profissional de saúde já tem uma consulta agendada para o novo horário proposto.");
        }

        consulta.setDataHora(novaDataHora);
        consulta.setStatus(StatusConsulta.REAGENDADA); // Atualiza o status para reagendada
        Consulta updatedConsulta = consultaRepository.save(consulta);
        return mapToResponseDTO(updatedConsulta);
    }

    @Transactional
    public void deletarConsulta(Long id) throws ChangeSetPersister.NotFoundException {
        if (!consultaRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }
        consultaRepository.deleteById(id);
    }

    // Método auxiliar para mapear Entidade Consulta para ConsultaResponseDTO
    private ConsultaResponseDTO mapToResponseDTO(Consulta consulta) {
        ConsultaResponseDTO responseDTO = new ConsultaResponseDTO();
        responseDTO.setId(consulta.getId());
        responseDTO.setDataHora(consulta.getDataHora());
        responseDTO.setStatus(consulta.getStatus());

        // Mapear dados do paciente
        if (consulta.getPaciente() != null) {
            responseDTO.setPacienteId(consulta.getPaciente().getId());
            responseDTO.setPacienteNome(consulta.getPaciente().getNomeCompleto());
            responseDTO.setPacienteCpf(consulta.getPaciente().getCpf());
        }

        // Mapear dados do profissional de saúde (usuário)
        if (consulta.getProfissionalSaude() != null) {
            responseDTO.setProfissionalSaudeId(consulta.getProfissionalSaude().getId());
            responseDTO.setProfissionalSaudeNome(consulta.getProfissionalSaude().getUsername()); // Usando username como nome
        }
        return responseDTO;
    }
}