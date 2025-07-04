// src/main/java/com/sghss/production/service/TipoExameService.java
package com.sghss.production.service;

import com.sghss.production.dto.exame.TipoExameRequestDTO;
import com.sghss.production.dto.exame.TipoExameResponseDTO;
import com.sghss.production.exception.DataConflictException;
import com.sghss.production.model.TipoExame;
import com.sghss.production.repository.TipoExameRepository;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException; // Para NotFoundException
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TipoExameService {

    private final TipoExameRepository tipoExameRepository;

    public TipoExameService(TipoExameRepository tipoExameRepository) {
        this.tipoExameRepository = tipoExameRepository;
    }

    @Transactional
    public TipoExameResponseDTO criarTipoExame(TipoExameRequestDTO requestDTO) {
        // Valida se já existe um tipo de exame com o mesmo nome
        if (tipoExameRepository.findByNome(requestDTO.getNome()).isPresent()) {
            throw new DataConflictException("Tipo de exame com o nome '" + requestDTO.getNome() + "' já existe.");
        }

        TipoExame tipoExame = new TipoExame(
                requestDTO.getNome(),
                requestDTO.getDescricao(),
                requestDTO.getUnidadeMedidaPadrao(),
                requestDTO.getValoresReferencia()
        );

        TipoExame savedTipoExame = tipoExameRepository.save(tipoExame);
        return mapToTipoExameResponseDTO(savedTipoExame);
    }

    @Transactional(readOnly = true)
    public TipoExameResponseDTO buscarTipoExamePorId(Long id) throws NotFoundException {
        TipoExame tipoExame = tipoExameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
        return mapToTipoExameResponseDTO(tipoExame);
    }

    @Transactional(readOnly = true)
    public List<TipoExameResponseDTO> listarTodosTiposExames() {
        return tipoExameRepository.findAll().stream()
                .map(this::mapToTipoExameResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TipoExameResponseDTO atualizarTipoExame(Long id, TipoExameRequestDTO requestDTO) throws NotFoundException {
        TipoExame existingTipoExame = tipoExameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());

        // Verifica se o nome está sendo alterado para um nome já existente em outro tipo de exame
        if (!requestDTO.getNome().equals(existingTipoExame.getNome()) &&
            tipoExameRepository.findByNome(requestDTO.getNome()).isPresent()) {
            throw new DataConflictException("Outro tipo de exame com o nome '" + requestDTO.getNome() + "' já existe.");
        }

        existingTipoExame.setNome(requestDTO.getNome());
        existingTipoExame.setDescricao(requestDTO.getDescricao());
        existingTipoExame.setUnidadeMedidaPadrao(requestDTO.getUnidadeMedidaPadrao());
        existingTipoExame.setValoresReferencia(requestDTO.getValoresReferencia());

        TipoExame updatedTipoExame = tipoExameRepository.save(existingTipoExame);
        return mapToTipoExameResponseDTO(updatedTipoExame);
    }

    @Transactional
    public void deletarTipoExame(Long id) throws NotFoundException {
        if (!tipoExameRepository.existsById(id)) {
            throw new NotFoundException();
        }
        tipoExameRepository.deleteById(id);
    }

    // Método auxiliar para mapear Entidade TipoExame para TipoExameResponseDTO
    private TipoExameResponseDTO mapToTipoExameResponseDTO(TipoExame tipoExame) {
        TipoExameResponseDTO responseDTO = new TipoExameResponseDTO();
        responseDTO.setId(tipoExame.getId());
        responseDTO.setNome(tipoExame.getNome());
        responseDTO.setDescricao(tipoExame.getDescricao());
        responseDTO.setUnidadeMedidaPadrao(tipoExame.getUnidadeMedidaPadrao());
        responseDTO.setValoresReferencia(tipoExame.getValoresReferencia());
        return responseDTO;
    }
}