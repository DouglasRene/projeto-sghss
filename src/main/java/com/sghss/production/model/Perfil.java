package com.sghss.production.model;

import org.springframework.security.core.GrantedAuthority;

public enum Perfil implements GrantedAuthority {
    ROLE_ADMIN,
    ROLE_PROFISSIONAL_SAUDE, // Alterado para refletir o nome do perfil
    ROLE_PACIENTE;

    @Override
    public String getAuthority() {
        return name();
    }
}