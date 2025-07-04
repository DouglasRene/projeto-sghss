// src/main/java/com/sghss/production/service/ExamePacienteService.java
package com.sghss.production.service;

import com.sghss.production.dto.exame.ExamePacienteRequestDTO;
import com.sghss.production.dto.exame.ExamePacienteResponseDTO;
import com.sghss.production.model.Consulta;
import com.sghss.production.model.ExamePaciente;
import com.sghss.production.model.Paciente;
import com.sghss.production.model.TipoExame;
import com.sghss.production.model.Usuario; // Certifique-se de importar Usuario
import com.sghss.production.repository.ConsultaRepository;
import com.sghss.production.repository.ExamePacienteRepository;
import com.sghss.production.repository.PacienteRepository;
import com.sghss.production.repository.TipoExameRepository;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamePacienteService {

    private final ExamePacienteRepository examePacienteRepository;
    private final PacienteRepository pacienteRepository;
    private final TipoExameRepository tipoExameRepository;
    private final UserService userService; // Usaremos o UserService para buscar o Usuario solicitante
    private final ConsultaRepository consultaRepository; // Para associar a consultas

    public ExamePacienteService(ExamePacienteRepository examePacienteRepository,
                                PacienteRepository pacienteRepository,
                                TipoExameRepository tipoExameRepository,
                                UserService userService,
                                ConsultaRepository consultaRepository) {
        this.examePacienteRepository = examePacienteRepository;
        this.pacienteRepository = pacienteRepository;
        this.tipoExameRepository = tipoExameRepository;
        this.userService = userService;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    public ExamePacienteResponseDTO registrarExamePaciente(ExamePacienteRequestDTO requestDTO, Long solicitadoPorUsuarioId) throws NotFoundException {
        // Validações de existência das entidades relacionadas
        Paciente paciente = pacienteRepository.findById(requestDTO.getPacienteId())
                .orElseThrow(() -> new NotFoundException());

        TipoExame tipoExame = tipoExameRepository.findById(requestDTO.getTipoExameId())
                .orElseThrow(() -> new NotFoundException());

        Usuario solicitante = userService.getUsuarioEntityById(solicitadoPorUsuarioId)
                .orElseThrow(() -> new NotFoundException());

        Consulta consulta = null;
        if (requestDTO.getConsultaId() != null) {
            consulta = consultaRepository.findById(requestDTO.getConsultaId())
                    .orElseThrow(() -> new NotFoundException());
        }

        ExamePaciente examePaciente = new ExamePaciente(
                paciente,
                tipoExame,
                requestDTO.getDataRealizacao(),
                requestDTO.getDataResultado(),
                requestDTO.getLaboratorio(),
                requestDTO.getResultados(),
                requestDTO.getObservacoes(),
                requestDTO.getStatus(),
                solicitante,
                consulta,
                requestDTO.getDocumentoUrl()
        );

        ExamePaciente savedExame = examePacienteRepository.save(examePaciente);
        return mapToExamePacienteResponseDTO(savedExame);
    }

    @Transactional(readOnly = true)
    public ExamePacienteResponseDTO buscarExamePacientePorId(Long id) throws NotFoundException {
        ExamePaciente examePaciente = examePacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        return mapToExamePacienteResponseDTO(examePaciente);
    }

    @Transactional(readOnly = true)
    public List<ExamePacienteResponseDTO> listarExamesPorPaciente(Long pacienteId) throws NotFoundException {
        // Valida se o paciente existe antes de buscar os exames
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new NotFoundException();
        }
        return examePacienteRepository.findByPacienteId(pacienteId).stream()
                .map(this::mapToExamePacienteResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ExamePacienteResponseDTO> buscarExamesPorPacienteETipo(Long pacienteId, String tipoExameNome) throws NotFoundException {
        if (!pacienteRepository.existsById(pacienteId)) {
            throw new NotFoundException();
        }
        return examePacienteRepository.findByPacienteIdAndTipoExameNomeContainingIgnoreCase(pacienteId, tipoExameNome).stream()
                .map(this::mapToExamePacienteResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExamePacienteResponseDTO atualizarExamePaciente(Long id, ExamePacienteRequestDTO requestDTO, Long atualizadoPorUsuarioId) throws NotFoundException {
        ExamePaciente existingExame = examePacienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());

        // Revalidações caso os IDs de paciente, tipo de exame ou consulta sejam alterados
        Paciente paciente = pacienteRepository.findById(requestDTO.getPacienteId())
                .orElseThrow(() -> new NotFoundException());

        TipoExame tipoExame = tipoExameRepository.findById(requestDTO.getTipoExameId())
                .orElseThrow(() -> new NotFoundException());

        Consulta consulta = null;
        if (requestDTO.getConsultaId() != null) {
            consulta = consultaRepository.findById(requestDTO.getConsultaId())
                    .orElseThrow(() -> new NotFoundException());
        }

        // Não deve permitir alterar o solicitante original, apenas os dados do exame
        // existingExame.setSolicitadoPor(userService.findUserById(atualizadoPorUsuarioId)); // Decidir se o 'atualizadoPor' é relevante ou se apenas o solicitante original permanece

        existingExame.setPaciente(paciente);
        existingExame.setTipoExame(tipoExame);
        existingExame.setDataRealizacao(requestDTO.getDataRealizacao());
        existingExame.setDataResultado(requestDTO.getDataResultado());
        existingExame.setLaboratorio(requestDTO.getLaboratorio());
        existingExame.setResultados(requestDTO.getResultados());
        existingExame.setObservacoes(requestDTO.getObservacoes());
        existingExame.setStatus(requestDTO.getStatus());
        existingExame.setConsulta(consulta);
        existingExame.setDocumentoUrl(requestDTO.getDocumentoUrl());

        ExamePaciente updatedExame = examePacienteRepository.save(existingExame);
        return mapToExamePacienteResponseDTO(updatedExame);
    }

    @Transactional
    public void deletarExamePaciente(Long id) throws NotFoundException {
        if (!examePacienteRepository.existsById(id)) {
            throw new NotFoundException();
        }
        examePacienteRepository.deleteById(id);
    }

    // Método auxiliar para mapear Entidade ExamePaciente para ExamePacienteResponseDTO
    private ExamePacienteResponseDTO mapToExamePacienteResponseDTO(ExamePaciente examePaciente) {
        ExamePacienteResponseDTO responseDTO = new ExamePacienteResponseDTO();
        responseDTO.setId(examePaciente.getId());
        responseDTO.setPacienteId(examePaciente.getPaciente().getId());
        responseDTO.setPacienteNome(examePaciente.getPaciente().getNomeCompleto()); // Assumindo método getNomeCompleto no Paciente
        responseDTO.setTipoExameId(examePaciente.getTipoExame().getId());
        responseDTO.setTipoExameNome(examePaciente.getTipoExame().getNome());
        responseDTO.setDataRealizacao(examePaciente.getDataRealizacao());
        responseDTO.setDataResultado(examePaciente.getDataResultado());
        responseDTO.setLaboratorio(examePaciente.getLaboratorio());
        responseDTO.setResultados(examePaciente.getResultados());
        responseDTO.setObservacoes(examePaciente.getObservacoes());
        responseDTO.setStatus(examePaciente.getStatus());
        responseDTO.setSolicitadoPorUsuarioId(examePaciente.getSolicitadoPor().getId());
        responseDTO.setSolicitadoPorUsername(examePaciente.getSolicitadoPor().getUsername());
        if (examePaciente.getConsulta() != null) {
            responseDTO.setConsultaId(examePaciente.getConsulta().getId());
            responseDTO.setConsultaDataHora(examePaciente.getConsulta().getDataHora()); // Assumindo getDataHora na Consulta
        }
        responseDTO.setDocumentoUrl(examePaciente.getDocumentoUrl());
        return responseDTO;
    }
}