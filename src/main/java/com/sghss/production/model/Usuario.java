package com.sghss.production.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Data // <<< Esta anotação gera os getters e setters
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Pode ser email ou CPF
    @Column(nullable = false)
    private String password; // Senha criptografada

    @ElementCollection(targetClass = Perfil.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_perfis", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    private Set<Perfil> perfis = new HashSet<>();

    private boolean enabled = true;
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", unique = true) // patient_id é a FK. unique = true se for 1-1
    private Paciente paciente; // Relacionamento com Paciente

    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.perfis.stream().collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}