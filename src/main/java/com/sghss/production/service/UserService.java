// src/main/java/com/sghss/production/service/UserService.java
package com.sghss.production.service;

import com.sghss.production.dto.user.UserRequestDTO;
import com.sghss.production.dto.user.UserResponseDTO;
import com.sghss.production.exception.DataConflictException;
import com.sghss.production.model.Perfil;
import com.sghss.production.model.Usuario;
import com.sghss.production.repository.UsuarioRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional; // <--- Importe Optional
import java.util.stream.Collectors;

@Service // É um componente de serviço Spring
public class UserService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // Validação de unicidade do username (email)
        if (usuarioRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new DataConflictException("Usuário com o email " + requestDTO.getUsername() + " já existe.");
        }

        Usuario newUsuario = new Usuario();
        newUsuario.setUsername(requestDTO.getUsername());
        newUsuario.setPassword(passwordEncoder.encode(requestDTO.getPassword())); // Codifica a senha
        newUsuario.setEnabled(requestDTO.isEnabled());

        // Mapeia Strings de perfil para o enum Perfil
        newUsuario.setPerfis(requestDTO.getPerfis().stream()
                                 .map(Perfil::valueOf) // Converte String (ex: "ROLE_ADMIN") para Perfil enum
                                 .collect(Collectors.toSet()));

        Usuario savedUser = usuarioRepository.save(newUsuario);
        return mapToUserResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(Long id) throws ChangeSetPersister.NotFoundException { // Adicione throws
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException()); // Lança NotFoundException
        return mapToUserResponseDTO(usuario);
    }

    // --- NOVO MÉTODO NECESSÁRIO PARA O HISTORICOMEDICOCONTROLLER ---
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    // ---------------------------------------------------------------


    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAllUsers() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    // Método auxiliar para mapear Entidade Usuario para UserResponseDTO
    private UserResponseDTO mapToUserResponseDTO(Usuario usuario) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(usuario.getId());
        responseDTO.setUsername(usuario.getUsername());
        responseDTO.setEnabled(usuario.isEnabled());
        // Mapeia o Set<Perfil> para Set<String> para a resposta
        responseDTO.setPerfis(usuario.getPerfis().stream()
                                     .map(Perfil::getAuthority) // Retorna a String (ex: "ROLE_ADMIN")
                                     .collect(Collectors.toSet()));
        return responseDTO;
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO requestDTO) throws ChangeSetPersister.NotFoundException {
        Usuario existingUser = usuarioRepository.findById(id)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // Validação se o username está sendo alterado para um que já existe
        if (!requestDTO.getUsername().equals(existingUser.getUsername()) &&
            usuarioRepository.findByUsername(requestDTO.getUsername()).isPresent()) {
            throw new DataConflictException("Usuário com o email " + requestDTO.getUsername() + " já existe.");
        }

        existingUser.setUsername(requestDTO.getUsername());
        // A senha deve ser atualizada separadamente se não for enviada no requestDTO completo
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }
        existingUser.setEnabled(requestDTO.isEnabled());
        existingUser.setPerfis(requestDTO.getPerfis().stream()
                                 .map(Perfil::valueOf)
                                 .collect(Collectors.toSet()));

        Usuario updatedUser = usuarioRepository.save(existingUser);
        return mapToUserResponseDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) throws ChangeSetPersister.NotFoundException {
        if (!usuarioRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }
        usuarioRepository.deleteById(id);
    }
    
    public Long getIdDoPacienteLogado() throws ChangeSetPersister.NotFoundException, IllegalStateException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalStateException("Nenhum usuário autenticado ou contexto de segurança inválido.");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String usernameLogado = userDetails.getUsername();

        Usuario usuarioLogado = findByUsername(usernameLogado)
                .orElseThrow(() -> new ChangeSetPersister.NotFoundException());

        // Verifica se o usuário tem o perfil de PACIENTE
        boolean isPaciente = usuarioLogado.getPerfis().contains(Perfil.ROLE_PACIENTE);

        if (!isPaciente) {
            throw new IllegalStateException("O usuário logado não possui o perfil de PACIENTE.");
        }

        // Verifica se o paciente está associado ao usuário
        if (usuarioLogado.getPaciente() == null) {
            throw new ChangeSetPersister.NotFoundException(); // Ou uma exceção mais específica
        }

        return usuarioLogado.getPaciente().getId();
    }
    
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioEntityById(Long id) {
        return usuarioRepository.findById(id);
    }
}