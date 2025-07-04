// src/main/java/com/sghss/production/service/PacienteService.java
package com.sghss.production.service;

import com.sghss.production.dto.paciente.PacienteRequestDTO;
import com.sghss.production.dto.paciente.PacienteResponseDTO;
import com.sghss.production.exception.DataConflictException; // << Importar
import com.sghss.production.model.Paciente;
import com.sghss.production.repository.PacienteRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public PacienteResponseDTO criarPaciente(PacienteRequestDTO requestDTO) {
        if (pacienteRepository.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new DataConflictException("Já existe um paciente cadastrado com este CPF: " + requestDTO.getCpf()); // << Lança a nova exceção
        }

        Paciente paciente = new Paciente();
        paciente.setNomeCompleto(requestDTO.getNomeCompleto());
        paciente.setCpf(requestDTO.getCpf());
        paciente.setDataNascimento(requestDTO.getDataNascimento());
        paciente.setTelefone(requestDTO.getTelefone());
        paciente.setEndereco(requestDTO.getEndereco());

        Paciente savedPaciente = pacienteRepository.save(paciente);
        return mapToResponseDTO(savedPaciente);
    }

    // ... (outros métodos como listarTodosPacientes, buscarPacientePorId, deletarPaciente, etc.)

    @Transactional
    public PacienteResponseDTO atualizarPaciente(Long id, PacienteRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        if (!requestDTO.getCpf().equals(pacienteExistente.getCpf()) &&
            pacienteRepository.findByCpf(requestDTO.getCpf()).isPresent()) {
            throw new DataConflictException("Já existe outro paciente cadastrado com o CPF informado: " + requestDTO.getCpf()); // << Lança a nova exceção
        }

        pacienteExistente.setNomeCompleto(requestDTO.getNomeCompleto());
        pacienteExistente.setCpf(requestDTO.getCpf());
        pacienteExistente.setDataNascimento(requestDTO.getDataNascimento());
        pacienteExistente.setTelefone(requestDTO.getTelefone());
        pacienteExistente.setEndereco(requestDTO.getEndereco());

        Paciente updatedPaciente = pacienteRepository.save(pacienteExistente);
        return mapToResponseDTO(updatedPaciente);
    }
    @Transactional // Garante que a operação de deleção seja transacional
    public void deletarPaciente(Long id) throws ChangeSetPersister.NotFoundException {
        // Verifica se o paciente existe antes de tentar deletar
        if (!pacienteRepository.existsById(id)) {
            // Lança a exceção de "Não Encontrado" se o ID não existir
            throw new ChangeSetPersister.NotFoundException();
        }
        // Se existir, procede com a deleção
        pacienteRepository.deleteById(id);
    }
 // Método auxiliar para mapear Entidade Paciente para PacienteResponseDTO
    private PacienteResponseDTO mapToResponseDTO(Paciente paciente) {
        PacienteResponseDTO responseDTO = new PacienteResponseDTO();
        responseDTO.setId(paciente.getId());
        responseDTO.setNomeCompleto(paciente.getNomeCompleto());
        responseDTO.setCpf(paciente.getCpf());
        responseDTO.setDataNascimento(paciente.getDataNascimento());
        responseDTO.setTelefone(paciente.getTelefone());
        responseDTO.setEndereco(paciente.getEndereco());
        return responseDTO;
    }
    
    
}